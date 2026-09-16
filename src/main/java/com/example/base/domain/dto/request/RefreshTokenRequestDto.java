package com.example.base.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RefreshTokenRequestDto(
        @Schema(description = "Refresh token")
        @NotBlank(message = "Refresh token không được để trống.")
        String refreshToken
) {
}
