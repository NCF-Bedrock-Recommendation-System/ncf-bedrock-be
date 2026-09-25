package com.example.base.service;

import com.example.base.domain.entity.OtpPurpose;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

public interface RedisService {

  void saveOtp(String identifier, OtpPurpose purpose, String otp, Duration otpTtl, Duration cooldownTtl);

  Optional<String> getOtp(String identifier, OtpPurpose purpose);

  void deleteOtp(String identifier, OtpPurpose purpose);

  boolean isOtpCooldownActive(String identifier, OtpPurpose purpose);

  void saveResetToken(String token, UUID userId, Duration ttl);

  Optional<UUID> getUserIdByResetToken(String token);

  void deleteResetToken(String token);

  void saveRefreshToken(String jwtId, String email, Duration ttl);

  boolean isRefreshTokenValid(String jwtId, String email);

  void deleteRefreshToken(String jwtId);
}
