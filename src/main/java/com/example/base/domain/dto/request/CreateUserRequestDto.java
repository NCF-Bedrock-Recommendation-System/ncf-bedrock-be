package com.example.base.domain.dto.request;

import com.example.base.domain.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequestDto(
        @Schema(description = "Họ và tên người dùng", example = "Nguyen Van A")
        @NotBlank(message = "Họ và tên không được để trống.")
        String fullName,

        @Schema(description = "Số điện thoại", example = "0912345678")
        @NotBlank(message = "Số điện thoại không được để trống.")
        @Pattern(
                regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$",
                message = "Số điện thoại không đúng định dạng."
        )
        String phone,

        @Schema(description = "Email người dùng", example = "user@gmail.com")
        @NotBlank(message = "Email không được để trống.")
        @Email(message = "Email không đúng định dạng.")
        String email,

        @Schema(description = "Mật khẩu", example = "User123@")
        @NotBlank(message = "Mật khẩu không được để trống.")
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[^A-Za-z0-9])(?=\\S+$).{6,}$",
                message = "Mật khẩu phải có từ 6 ký tự trở lên, bao gồm ký tự in hoa và ký tự đặc biệt."
        )
        String password,

        @Schema(description = "Xác nhận mật khẩu", example = "User123@")
        @NotBlank(message = "Xác nhận mật khẩu không được để trống.")
        String confirmPassword,

        @Schema(description = "Vai trò người dùng", example = "USER")
        @NotNull(message = "Vai trò không được để trống.")
        Role role
) {
        @AssertTrue(message = "Xác nhận mật khẩu phải trùng khớp với mật khẩu.")
        public boolean isPasswordConfirmed() {
                return password != null && password.equals(confirmPassword);
        }
}
