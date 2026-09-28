package com.example.base.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record VerifyResetOtpRequestDto(
        @Schema(description = "Email hoặc số điện thoại", example = "user@gmail.com")
        @NotBlank(message = "Email hoặc số điện thoại không được để trống.")
        String identifier,

        @Schema(description = "Mã OTP gồm 6 số", example = "123456")
        @NotBlank(message = "OTP không được để trống.")
        @Pattern(regexp = "^[0-9]{6}$", message = "OTP phải gồm 6 chữ số.")
        String otp
) {
}
