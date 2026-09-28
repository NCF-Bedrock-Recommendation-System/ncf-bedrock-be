package com.example.base.controller;

import com.example.base.common.response.ApiResponse;
import com.example.base.constant.ApiPath;
import com.example.base.constant.SuccessMessage;
import com.example.base.domain.dto.request.*;
import com.example.base.domain.dto.response.LoginResponseDto;
import com.example.base.domain.dto.response.ResetTokenResponseDto;
import com.example.base.domain.dto.response.UserResponseDto;
import com.example.base.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.API_NOW + "/auth")
@Tag(name = "Auth Controller", description = "Đăng ký, đăng nhập, đăng xuất và refresh token")
public class AuthController {

  private final AuthService authService;

  @PostMapping("/register")
  @Operation(summary = "Đăng ký tài khoản")
  public ResponseEntity<ApiResponse<UserResponseDto>> register(
          @Valid @RequestBody RegisterRequestDto request
  ) {
    return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.created(
                    SuccessMessage.Auth.REGISTER_SUCCESS,
                    authService.register(request)
            ));
  }

  @PostMapping("/verify-otp")
  @Operation(summary = "Xác thực OTP đăng ký")
  public ResponseEntity<ApiResponse<Void>> verifyOtp(
          @Valid @RequestBody VerifyOtpRequestDto request
  ) {
    authService.verifyOtp(request);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(SuccessMessage.Auth.VERIFY_OTP_SUCCESS));
  }

  @PostMapping("/resend-otp")
  @Operation(summary = "Gửi lại OTP đăng ký")
  public ResponseEntity<ApiResponse<Void>> resendOtp(
          @Valid @RequestBody ResendOtpRequestDto request
  ) {
    authService.resendOtp(request);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(SuccessMessage.Auth.RESEND_OTP_SUCCESS));
  }

  @PostMapping("/login")
  @Operation(summary = "Đăng nhập", description = "Email hoặc số điện thoại + mật khẩu -> nhận JWT")
  public ResponseEntity<ApiResponse<LoginResponseDto>> login(
          @Valid @RequestBody LoginRequestDto request
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(
                    SuccessMessage.Auth.LOGIN_SUCCESS,
                    authService.login(request)
            ));
  }

  @PostMapping("/refresh")
  @Operation(summary = "Làm mới access token")
  public ResponseEntity<ApiResponse<LoginResponseDto>> refreshToken(
          @Valid @RequestBody RefreshTokenRequestDto request
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(
                    SuccessMessage.Auth.REFRESH_TOKEN_SUCCESS,
                    authService.refreshToken(request)
            ));
  }

  @PostMapping("/logout")
  @Operation(summary = "Đăng xuất")
  public ResponseEntity<ApiResponse<Void>> logout(
          @Valid @RequestBody LogoutRequestDto request
  ) {
    authService.logout(request);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(SuccessMessage.Auth.LOGOUT_SUCCESS));
  }

  @PostMapping("/oauth")
  @Operation(summary = "Đăng nhập OAuth Google/Microsoft")
  public ResponseEntity<ApiResponse<LoginResponseDto>> oauth(
          @Valid @RequestBody OAuthLoginRequestDto request
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(
                    SuccessMessage.Auth.OAUTH_LOGIN_SUCCESS,
                    authService.oauth(request)
            ));
  }

  @PostMapping("/forgot-password")
  @Operation(summary = "Quên mật khẩu")
  public ResponseEntity<ApiResponse<Void>> forgotPassword(
          @Valid @RequestBody ForgotPasswordRequestDto request
  ) {
    authService.forgotPassword(request);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(SuccessMessage.Auth.FORGOT_PASSWORD_SUCCESS));
  }

  @PostMapping("/verify-reset-otp")
  @Operation(summary = "Xác thực OTP reset mật khẩu")
  public ResponseEntity<ApiResponse<ResetTokenResponseDto>> verifyResetOtp(
          @Valid @RequestBody VerifyResetOtpRequestDto request
  ) {
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(
                    SuccessMessage.Auth.VERIFY_RESET_OTP_SUCCESS,
                    authService.verifyResetOtp(request)
            ));
  }

  @PostMapping("/reset-password")
  @Operation(summary = "Đổi mật khẩu mới")
  public ResponseEntity<ApiResponse<Void>> resetPassword(
          @Valid @RequestBody ResetPasswordRequestDto request
  ) {
    authService.resetPassword(request);
    return ResponseEntity
            .status(HttpStatus.OK)
            .body(ApiResponse.success(SuccessMessage.Auth.RESET_PASSWORD_SUCCESS));
  }
}
