package com.example.base.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequestDto(
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
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=\\S+$).{8,}$",
                message = "Mật khẩu tối thiểu 8 ký tự, có chữ thường và số."
        )
        String password,

        @Schema(description = "Xác nhận mật khẩu", example = "User123@")
        @NotBlank(message = "Xác nhận mật khẩu không được để trống.")
        String confirmPassword
) {
        @AssertTrue(message = "Xác nhận mật khẩu phải trùng khớp với mật khẩu.")
        public boolean isPasswordConfirmed() {
                return password != null && password.equals(confirmPassword);
        }
}
