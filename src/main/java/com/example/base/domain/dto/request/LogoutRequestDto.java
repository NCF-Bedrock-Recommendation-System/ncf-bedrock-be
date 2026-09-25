package com.example.base.domain.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LogoutRequestDto(
        @NotBlank(message = "Refresh token không được để trống.")
        String refreshToken
) {
}
