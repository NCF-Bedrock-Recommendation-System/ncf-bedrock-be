package com.example.base.domain.dto.response;

import com.example.base.constant.CommonConstant;
import lombok.Builder;

@Builder
public record LoginResponseDto(
        String accessToken,
        String refreshToken,
        UserResponseDto user,
        String tokenType
) {
    public LoginResponseDto {
        if (tokenType == null) {
            tokenType = CommonConstant.BEARER_TOKEN;
        }
    }
}
