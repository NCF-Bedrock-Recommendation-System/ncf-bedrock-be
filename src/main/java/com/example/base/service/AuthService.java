package com.example.base.service;

import com.example.base.domain.dto.request.*;
import com.example.base.domain.dto.response.LoginResponseDto;
import com.example.base.domain.dto.response.ResetTokenResponseDto;
import com.example.base.domain.dto.response.UserResponseDto;

public interface AuthService {

  UserResponseDto register(RegisterRequestDto request);

  void verifyOtp(VerifyOtpRequestDto request);

  void resendOtp(ResendOtpRequestDto request);

  LoginResponseDto login(LoginRequestDto request);

  LoginResponseDto refreshToken(RefreshTokenRequestDto request);

  void logout(LogoutRequestDto request);

  LoginResponseDto oauth(OAuthLoginRequestDto request);

  void forgotPassword(ForgotPasswordRequestDto request);

  ResetTokenResponseDto verifyResetOtp(VerifyResetOtpRequestDto request);

  void resetPassword(ResetPasswordRequestDto request);
}
