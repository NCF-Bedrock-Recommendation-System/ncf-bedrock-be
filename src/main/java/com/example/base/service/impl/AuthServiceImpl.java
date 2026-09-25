package com.example.base.service.impl;

import com.example.base.constant.CommonConstant;
import com.example.base.constant.ErrorMessage;
import com.example.base.domain.dto.request.*;
import com.example.base.domain.dto.response.LoginResponseDto;
import com.example.base.domain.dto.response.ResetTokenResponseDto;
import com.example.base.domain.dto.response.UserResponseDto;
import com.example.base.domain.entity.*;
import com.example.base.domain.mapper.UserMapper;
import com.example.base.exception.nonRetryException.BadRequestException;
import com.example.base.exception.nonRetryException.UnauthorizedException;
import com.example.base.repository.UserRepository;
import com.example.base.security.JwtProvider;
import com.example.base.service.AuthService;
import com.example.base.service.RedisService;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import com.example.base.service.EmailService;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthServiceImpl implements AuthService {

  UserRepository userRepository;
  JwtProvider jwtProvider;
  RedisService redisService;
  UserDetailsService userDetailsService;
  UserMapper userMapper;
  PasswordEncoder passwordEncoder;
  EmailService emailService;
  RestClient restClient = RestClient.create();

  static final int OTP_EXPIRATION_MINUTES = 5;
  static final int OTP_RESEND_COOLDOWN_SECONDS = 60;
  static final int RESET_TOKEN_EXPIRATION_MINUTES = 10;
  static final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";
  static final String GOOGLE_USER_INFO_URI = "https://openidconnect.googleapis.com/v1/userinfo";
  static final String MICROSOFT_TOKEN_URI = "https://login.microsoftonline.com/%s/oauth2/v2.0/token";
  static final String MICROSOFT_USER_INFO_URI = "https://graph.microsoft.com/oidc/userinfo";
  static final SecureRandom OTP_RANDOM = new SecureRandom();

  @NonFinal
  @Value("${jwt.access.expiration_time}")
  long accessTokenExpiration;

  @NonFinal
  @Value("${jwt.refresh.expiration_time}")
  long refreshTokenExpiration;

  @NonFinal
  @Value("${oauth.google.client-id:}")
  String googleClientId;

  @NonFinal
  @Value("${oauth.google.client-secret:}")
  String googleClientSecret;

  @NonFinal
  @Value("${oauth.google.redirect-uri:}")
  String googleRedirectUri;

  @NonFinal
  @Value("${oauth.microsoft.client-id:}")
  String microsoftClientId;

  @NonFinal
  @Value("${oauth.microsoft.client-secret:}")
  String microsoftClientSecret;

  @NonFinal
  @Value("${oauth.microsoft.tenant-id:common}")
  String microsoftTenantId;

  @NonFinal
  @Value("${oauth.microsoft.redirect-uri:}")
  String microsoftRedirectUri;

  @Override
  @Transactional
  public UserResponseDto register(RegisterRequestDto request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new BadRequestException(ErrorMessage.User.ERR_EMAIL_EXISTED);
    }
    if (userRepository.existsByPhone(request.phone())) {
      throw new BadRequestException(ErrorMessage.User.ERR_PHONE_EXISTED);
    }

    User user = User.builder()
            .fullName(request.fullName())
            .phone(request.phone())
            .email(request.email())
            .passwordHash(passwordEncoder.encode(request.password()))
            .role(Role.USER)
            .status(UserStatus.PENDING_VERIFICATION)
            .enabled(true)
            .build();

    User savedUser = userRepository.save(user);
    generateAndSendOtp(savedUser.getEmail(), OtpPurpose.REGISTER);

    return userMapper.from(savedUser);
  }

  @Override
  @Transactional
  public void verifyOtp(VerifyOtpRequestDto request) {
    User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadRequestException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

    validateOtp(request.email(), request.otp(), OtpPurpose.REGISTER);
    redisService.deleteOtp(request.email(), OtpPurpose.REGISTER);

    user.setStatus(UserStatus.ACTIVE);
    user.setEnabled(true);
    userRepository.save(user);
  }

  @Override
  @Transactional
  public void resendOtp(ResendOtpRequestDto request) {
    User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new BadRequestException(ErrorMessage.User.ERR_USER_NOT_EXISTED));

    if (UserStatus.ACTIVE.equals(user.getStatus())) {
      throw new BadRequestException("Tài khoản đã được xác thực.");
    }

    validateOtpCooldown(request.email(), OtpPurpose.REGISTER);
    generateAndSendOtp(request.email(), OtpPurpose.REGISTER);
  }

  @Override
  @Transactional(readOnly = true)
  public LoginResponseDto login(LoginRequestDto request) {
    User user = findByIdentifier(request.identifier());

    if (!Boolean.TRUE.equals(user.getEnabled())) {
      throw new UnauthorizedException(ErrorMessage.User.ERR_USER_DISABLED);
    }
    if (!UserStatus.ACTIVE.equals(user.getStatus())) {
      throw new UnauthorizedException(ErrorMessage.User.ERR_USER_NOT_ACTIVE);
    }

    if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
      throw new UnauthorizedException(ErrorMessage.Auth.ERR_INVALID_CREDENTIALS);
    }

    return buildLoginResponse(user);
  }

  @Override
  @Transactional(readOnly = true)
  public LoginResponseDto refreshToken(RefreshTokenRequestDto request) {
    String refreshToken = request.refreshToken();

    if (!"REFRESH".equals(jwtProvider.extractTokenType(refreshToken))) {
      throw new UnauthorizedException(ErrorMessage.Auth.INVALID_REFRESH_TOKEN);
    }

    String email = jwtProvider.extractEmail(refreshToken);
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    if (!jwtProvider.isTokenValid(refreshToken, userDetails)) {
      throw new UnauthorizedException(ErrorMessage.Auth.INVALID_REFRESH_TOKEN);
    }
    if (!redisService.isRefreshTokenValid(jwtProvider.extractTokenId(refreshToken), email)) {
      throw new UnauthorizedException(ErrorMessage.Auth.INVALID_REFRESH_TOKEN);
    }

    User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UnauthorizedException(ErrorMessage.Auth.INVALID_REFRESH_TOKEN));

    redisService.deleteRefreshToken(jwtProvider.extractTokenId(refreshToken));
    return buildLoginResponse(user);
  }

  @Override
  public void logout(LogoutRequestDto request) {
    if (!"REFRESH".equals(jwtProvider.extractTokenType(request.refreshToken()))) {
      throw new UnauthorizedException(ErrorMessage.Auth.INVALID_REFRESH_TOKEN);
    }

    String email = jwtProvider.extractEmail(request.refreshToken());
    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

    if (!jwtProvider.isTokenValid(request.refreshToken(), userDetails)) {
      throw new UnauthorizedException(ErrorMessage.Auth.ERR_TOKEN_INVALIDATED);
    }

    redisService.deleteRefreshToken(jwtProvider.extractTokenId(request.refreshToken()));
  }

  @Override
  @Transactional
  public LoginResponseDto oauth(OAuthLoginRequestDto request) {
    OAuthUserInfo oauthUser = switch (request.provider()) {
      case GOOGLE -> getGoogleUserInfo(request.authorizationCode());
      case MICROSOFT -> getMicrosoftUserInfo(request.authorizationCode());
    };

    User user = userRepository.findByEmail(oauthUser.email())
            .map(existingUser -> {
              if (!Boolean.TRUE.equals(existingUser.getEnabled()) || UserStatus.DISABLED.equals(existingUser.getStatus())) {
                throw new UnauthorizedException(ErrorMessage.User.ERR_USER_DISABLED);
              }
              if (!UserStatus.ACTIVE.equals(existingUser.getStatus())) {
                existingUser.setStatus(UserStatus.ACTIVE);
                existingUser.setEnabled(true);
                return userRepository.save(existingUser);
              }
              return existingUser;
            })
            .orElseGet(() -> createOAuthUser(oauthUser));

    return buildLoginResponse(user);
  }

  @Override
  @Transactional
  public void forgotPassword(ForgotPasswordRequestDto request) {
    User user = findByIdentifier(request.identifier());

    if (!UserStatus.ACTIVE.equals(user.getStatus())) {
      throw new UnauthorizedException(ErrorMessage.User.ERR_USER_NOT_ACTIVE);
    }

    validateOtpCooldown(request.identifier(), OtpPurpose.RESET_PASSWORD);
    generateAndSendOtp(request.identifier(), OtpPurpose.RESET_PASSWORD);
  }

  @Override
  @Transactional
  public ResetTokenResponseDto verifyResetOtp(VerifyResetOtpRequestDto request) {
    User user = findByIdentifier(request.identifier());
    validateOtp(request.identifier(), request.otp(), OtpPurpose.RESET_PASSWORD);
    redisService.deleteOtp(request.identifier(), OtpPurpose.RESET_PASSWORD);

    String resetToken = UUID.randomUUID().toString();
    redisService.saveResetToken(
            resetToken,
            user.getId(),
            Duration.ofMinutes(RESET_TOKEN_EXPIRATION_MINUTES)
    );

    return new ResetTokenResponseDto(resetToken);
  }

  @Override
  @Transactional
  public void resetPassword(ResetPasswordRequestDto request) {
    UUID userId = redisService.getUserIdByResetToken(request.resetToken())
            .orElseThrow(() -> new BadRequestException(ErrorMessage.Auth.ERR_RESET_TOKEN_INVALID));

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException(ErrorMessage.User.ERR_USER_NOT_EXISTED));
    user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
    userRepository.save(user);

    redisService.deleteResetToken(request.resetToken());
  }

  private User findByIdentifier(String identifier) {
    return userRepository.findByEmail(identifier)
            .or(() -> userRepository.findByPhone(identifier))
            .orElseThrow(() -> new UnauthorizedException(ErrorMessage.Auth.ERR_INVALID_CREDENTIALS));
  }

  private void validateOtp(String identifier, String otp, OtpPurpose purpose) {
    String storedOtp = redisService.getOtp(identifier, purpose)
            .orElseThrow(() -> new BadRequestException(ErrorMessage.Auth.ERR_OTP_NOT_FOUND));

    if (!storedOtp.equals(otp)) {
      throw new BadRequestException(ErrorMessage.Auth.ERR_OTP_NOT_FOUND);
    }
  }

  private void validateOtpCooldown(String identifier, OtpPurpose purpose) {
    if (redisService.isOtpCooldownActive(identifier, purpose)) {
      throw new BadRequestException(ErrorMessage.Auth.ERR_OTP_COOLDOWN);
    }
  }

  private void generateAndSendOtp(String identifier, OtpPurpose purpose) {
    String otp = String.format("%06d", OTP_RANDOM.nextInt(1_000_000));
    redisService.saveOtp(
            identifier,
            purpose,
            otp,
            Duration.ofMinutes(OTP_EXPIRATION_MINUTES),
            Duration.ofSeconds(OTP_RESEND_COOLDOWN_SECONDS)
    );
    emailService.sendOtp(identifier, otp);
    log.info("OTP {} sent to {}", purpose, identifier);
  }

  private OAuthUserInfo getGoogleUserInfo(String authorizationCode) {
    assertOAuthConfigured(googleClientId, googleClientSecret, googleRedirectUri);

    MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
    tokenRequest.add("grant_type", "authorization_code");
    tokenRequest.add("code", authorizationCode);
    tokenRequest.add("client_id", googleClientId);
    tokenRequest.add("client_secret", googleClientSecret);
    tokenRequest.add("redirect_uri", googleRedirectUri);

    JsonNode tokenResponse = exchangeAuthorizationCode(GOOGLE_TOKEN_URI, tokenRequest);
    String accessToken = requiredText(tokenResponse, "access_token", ErrorMessage.Auth.ERR_OAUTH_INVALID_CODE);

    JsonNode userInfo = fetchUserInfo(GOOGLE_USER_INFO_URI, accessToken);
    if (userInfo.has("email_verified") && !userInfo.path("email_verified").asBoolean()) {
      throw new UnauthorizedException(ErrorMessage.Auth.ERR_OAUTH_EMAIL_NOT_VERIFIED);
    }

    return new OAuthUserInfo(
            requiredText(userInfo, "email", ErrorMessage.Auth.ERR_OAUTH_EMAIL_NOT_FOUND),
            textOrDefault(userInfo, "name", null)
    );
  }

  private OAuthUserInfo getMicrosoftUserInfo(String authorizationCode) {
    assertOAuthConfigured(microsoftClientId, microsoftClientSecret, microsoftRedirectUri);

    MultiValueMap<String, String> tokenRequest = new LinkedMultiValueMap<>();
    tokenRequest.add("grant_type", "authorization_code");
    tokenRequest.add("code", authorizationCode);
    tokenRequest.add("client_id", microsoftClientId);
    tokenRequest.add("client_secret", microsoftClientSecret);
    tokenRequest.add("redirect_uri", microsoftRedirectUri);
    tokenRequest.add("scope", "openid profile email User.Read");

    JsonNode tokenResponse = exchangeAuthorizationCode(
            MICROSOFT_TOKEN_URI.formatted(microsoftTenantId),
            tokenRequest
    );
    String accessToken = requiredText(tokenResponse, "access_token", ErrorMessage.Auth.ERR_OAUTH_INVALID_CODE);

    JsonNode userInfo = fetchUserInfo(MICROSOFT_USER_INFO_URI, accessToken);
    String email = firstText(userInfo, "email", "preferred_username", "upn");
    if (email == null || email.isBlank()) {
      throw new BadRequestException(ErrorMessage.Auth.ERR_OAUTH_EMAIL_NOT_FOUND);
    }

    return new OAuthUserInfo(email, textOrDefault(userInfo, "name", null));
  }

  private JsonNode exchangeAuthorizationCode(String tokenUri, MultiValueMap<String, String> tokenRequest) {
    try {
      return restClient.post()
              .uri(tokenUri)
              .contentType(MediaType.APPLICATION_FORM_URLENCODED)
              .body(tokenRequest)
              .retrieve()
              .body(JsonNode.class);
    } catch (RestClientResponseException e) {
      log.warn("OAuth token exchange failed: {}", e.getResponseBodyAsString());
      throw new BadRequestException(ErrorMessage.Auth.ERR_OAUTH_INVALID_CODE);
    }
  }

  private JsonNode fetchUserInfo(String userInfoUri, String accessToken) {
    try {
      return restClient.get()
              .uri(userInfoUri)
              .headers(headers -> headers.setBearerAuth(accessToken))
              .retrieve()
              .body(JsonNode.class);
    } catch (RestClientResponseException e) {
      log.warn("OAuth user info fetch failed: {}", e.getResponseBodyAsString());
      throw new BadRequestException(ErrorMessage.Auth.ERR_OAUTH_INVALID_CODE);
    }
  }

  private User createOAuthUser(OAuthUserInfo oauthUser) {
    User user = User.builder()
            .fullName(resolveOAuthFullName(oauthUser))
            .email(oauthUser.email())
            .phone(generateOAuthPhonePlaceholder())
            .passwordHash(passwordEncoder.encode(UUID.randomUUID().toString()))
            .role(Role.USER)
            .status(UserStatus.ACTIVE)
            .enabled(true)
            .build();

    return userRepository.save(user);
  }

  private String resolveOAuthFullName(OAuthUserInfo oauthUser) {
    if (oauthUser.fullName() != null && !oauthUser.fullName().isBlank()) {
      return oauthUser.fullName();
    }
    int atIndex = oauthUser.email().indexOf("@");
    return atIndex > 0 ? oauthUser.email().substring(0, atIndex) : oauthUser.email();
  }

  private String generateOAuthPhonePlaceholder() {
    return "oauth-" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
  }

  private void assertOAuthConfigured(String clientId, String clientSecret, String redirectUri) {
    if (isBlank(clientId) || isBlank(clientSecret) || isBlank(redirectUri)) {
      throw new BadRequestException(ErrorMessage.Auth.ERR_OAUTH_NOT_CONFIGURED);
    }
  }

  private String requiredText(JsonNode node, String field, String errorMessage) {
    String value = textOrDefault(node, field, null);
    if (value == null || value.isBlank()) {
      throw new BadRequestException(errorMessage);
    }
    return value;
  }

  private String firstText(JsonNode node, String... fields) {
    for (String field : fields) {
      String value = textOrDefault(node, field, null);
      if (value != null && !value.isBlank()) {
        return value;
      }
    }
    return null;
  }

  private String textOrDefault(JsonNode node, String field, String defaultValue) {
    if (node == null || !node.hasNonNull(field)) {
      return defaultValue;
    }
    return node.path(field).asText(defaultValue);
  }

  private boolean isBlank(String value) {
    return value == null || value.isBlank();
  }

  private LoginResponseDto buildLoginResponse(User user) {
    String accessToken = jwtProvider.generateToken(user, accessTokenExpiration, "ACCESS");
    String refreshToken = jwtProvider.generateToken(user, refreshTokenExpiration, "REFRESH");
    redisService.saveRefreshToken(
            jwtProvider.extractTokenId(refreshToken),
            user.getEmail(),
            Duration.ofMillis(refreshTokenExpiration)
    );

    return LoginResponseDto.builder()
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .user(userMapper.from(user))
            .tokenType(CommonConstant.BEARER_TOKEN)
            .build();
  }

  private record OAuthUserInfo(String email, String fullName) {
  }
}
