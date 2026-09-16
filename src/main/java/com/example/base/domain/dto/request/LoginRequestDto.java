package com.example.base.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Schema(description = "Email", example = "user@gmail.com")
        @NotBlank(message = "Email không được để trống.")
        @Email(message = "Email không đúng định dạng.")
        String email,

        @Schema(description = "Mật khẩu", example = "User123@")
        @NotBlank(message = "Mật khẩu không được để trống.")
        String password
) {
}
