package com.example.base.domain.dto.request;

import com.example.base.domain.entity.OAuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record OAuthLoginRequestDto(
        @Schema(description = "Nhà cung cấp OAuth", example = "GOOGLE")
        @NotNull(message = "Provider không được để trống.")
        OAuthProvider provider,

        @Schema(description = "Authorization code từ provider")
        @NotBlank(message = "Authorization code không được để trống.")
        String authorizationCode
) {
}
