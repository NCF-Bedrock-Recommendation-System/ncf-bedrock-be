package com.example.base.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record ForgotPasswordRequestDto(
        @Schema(description = "Email hoặc số điện thoại", example = "user@gmail.com")
        @NotBlank(message = "Email hoặc số điện thoại không được để trống.")
        String identifier
) {
}
