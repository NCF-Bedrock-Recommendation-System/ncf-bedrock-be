package com.example.base.domain.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;

public record UpdateUserRequestDto(
        @Schema(description = "Họ và tên người dùng", example = "Nguyen Van A")
        String fullName,

        @Schema(description = "Số điện thoại", example = "0912345678")
        @Pattern(
                regexp = "^(0|\\+84)(3|5|7|8|9)[0-9]{8}$",
                message = "Số điện thoại không đúng định dạng."
        )
        String phone,

        @Schema(description = "Trạng thái tài khoản", example = "true")
        Boolean enabled
) {
}
