package com.example.base.security;

import com.example.base.domain.entity.User;
import com.example.base.constant.ErrorMessage;
import com.example.base.exception.nonRetryException.BadRequestException;
import com.example.base.repository.InvalidatedTokenRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@Component
@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtProvider {

  InvalidatedTokenRepository invalidatedTokenRepository;

  @NonFinal
  @Value("${jwt.secret}")
  String secretKey;

  public String generateToken(User user, long expirationTime, String tokenType) {
    try {
      JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
          .subject(user.getEmail())
          .issueTime(new Date())
          .expirationTime(new Date(System.currentTimeMillis() + expirationTime))
          .jwtID(UUID.randomUUID().toString())
          .claim("authorities", List.of("ROLE_" + user.getRole().name()))
          .claim("userId", user.getId())
          .claim("email", user.getEmail())
          .claim("tokenType", tokenType)
          .build();

      SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
      signedJWT.sign(new MACSigner(secretKey.getBytes()));

      return signedJWT.serialize();

    } catch (JOSEException e) {
      throw new BadRequestException(ErrorMessage.Auth.ERR_MALFORMED_TOKEN);
    }
  }

  // Get the signing key for JWT operations
  private Key getSignInKey() {
    return Keys.hmacShaKeyFor(secretKey.getBytes());
  }

  public String extractEmail(String token) {
    return extractClaim(token, Claims::getSubject);
  }

  public String extractUsername(String token) {
    return extractEmail(token);
  }

  // Extract the expiration date from the JWT token
  public Date extractExpiration(String token) {
    return extractClaim(token, Claims::getExpiration);
  }

  // Extract a specific claim from the JWT token using a claims resolver function
  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  public boolean isTokenValid(String token, UserDetails userDetails) {
    final String username = extractUsername(token);
    final String jwtId = extractTokenId(token);
    boolean isInvalidated = invalidatedTokenRepository.existsById(jwtId);
    return (username.equals(userDetails.getUsername())) && !isTokenExpired(token) && !isInvalidated;
  }

  public boolean isTokenExpired(String token) {
    return extractExpiration(token).before(new Date());
  }

  public String extractTokenId(String token) {
    return extractClaim(token, Claims::getId);
  }

  public String extractTokenType(String token) {
    return extractClaim(token, claims -> claims.get("tokenType", String.class));
  }

  private Claims extractAllClaims(String token) {
    return Jwts
        .parserBuilder()
        .setSigningKey(getSignInKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
