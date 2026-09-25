package com.example.base.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ResetPasswordRequestDto(
        @Schema(description = "Reset token")
        @NotBlank(message = "Reset token không được để trống.")
        String resetToken,

        @Schema(description = "Mật khẩu mới", example = "NewUser123@")
        @NotBlank(message = "Mật khẩu mới không được để trống.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=\\S+$).{6,}$",
                message = "Mật khẩu phải có từ 6 ký tự trở lên, bao gồm ký tự in hoa và ký tự đặc biệt."
        )
        String newPassword,

        @Schema(description = "Xác nhận mật khẩu mới", example = "NewUser123@")
        @NotBlank(message = "Xác nhận mật khẩu không được để trống.")
        String confirmPassword
) {
        @AssertTrue(message = "Mật khẩu không trùng khớp.")
        public boolean isPasswordConfirmed() {
                return newPassword != null && newPassword.equals(confirmPassword);
        }
}
