package com.example.base.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResendOtpRequestDto(
        @Schema(description = "Email người dùng", example = "user@gmail.com")
        @NotBlank(message = "Email không được để trống.")
        @Email(message = "Email không đúng định dạng.")
        String email
) {
}
