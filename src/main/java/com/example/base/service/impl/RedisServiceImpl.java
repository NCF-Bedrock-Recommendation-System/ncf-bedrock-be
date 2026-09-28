package com.example.base.service.impl;

import com.example.base.domain.entity.OtpPurpose;
import com.example.base.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {

  private static final String OTP_KEY = "auth:otp:%s:%s";
  private static final String OTP_COOLDOWN_KEY = "auth:otp-cooldown:%s:%s";
  private static final String RESET_TOKEN_KEY = "auth:reset-token:%s";
  private static final String REFRESH_TOKEN_KEY = "auth:refresh-token:%s";

  private final StringRedisTemplate redisTemplate;

  @Override
  public void saveOtp(String identifier, OtpPurpose purpose, String otp, Duration otpTtl, Duration cooldownTtl) {
    redisTemplate.opsForValue().set(otpKey(identifier, purpose), otp, otpTtl);
    redisTemplate.opsForValue().set(otpCooldownKey(identifier, purpose), "1", cooldownTtl);
  }

  @Override
  public Optional<String> getOtp(String identifier, OtpPurpose purpose) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(otpKey(identifier, purpose)));
  }

  @Override
  public void deleteOtp(String identifier, OtpPurpose purpose) {
    redisTemplate.delete(otpKey(identifier, purpose));
  }

  @Override
  public boolean isOtpCooldownActive(String identifier, OtpPurpose purpose) {
    return Boolean.TRUE.equals(redisTemplate.hasKey(otpCooldownKey(identifier, purpose)));
  }

  @Override
  public void saveResetToken(String token, UUID userId, Duration ttl) {
    redisTemplate.opsForValue().set(resetTokenKey(token), userId.toString(), ttl);
  }

  @Override
  public Optional<UUID> getUserIdByResetToken(String token) {
    return Optional.ofNullable(redisTemplate.opsForValue().get(resetTokenKey(token)))
            .map(UUID::fromString);
  }

  @Override
  public void deleteResetToken(String token) {
    redisTemplate.delete(resetTokenKey(token));
  }

  @Override
  public void saveRefreshToken(String jwtId, String email, Duration ttl) {
    if (ttl.isZero() || ttl.isNegative()) {
      return;
    }
    redisTemplate.opsForValue().set(refreshTokenKey(jwtId), email, ttl);
  }

  @Override
  public boolean isRefreshTokenValid(String jwtId, String email) {
    return email.equals(redisTemplate.opsForValue().get(refreshTokenKey(jwtId)));
  }

  @Override
  public void deleteRefreshToken(String jwtId) {
    redisTemplate.delete(refreshTokenKey(jwtId));
  }

  private String otpKey(String identifier, OtpPurpose purpose) {
    return OTP_KEY.formatted(purpose.name(), encode(identifier));
  }

  private String otpCooldownKey(String identifier, OtpPurpose purpose) {
    return OTP_COOLDOWN_KEY.formatted(purpose.name(), encode(identifier));
  }

  private String resetTokenKey(String token) {
    return RESET_TOKEN_KEY.formatted(token);
  }

  private String refreshTokenKey(String jwtId) {
    return REFRESH_TOKEN_KEY.formatted(jwtId);
  }

  private String encode(String value) {
    return Base64.getUrlEncoder()
            .withoutPadding()
            .encodeToString(value.getBytes(StandardCharsets.UTF_8));
  }
}
