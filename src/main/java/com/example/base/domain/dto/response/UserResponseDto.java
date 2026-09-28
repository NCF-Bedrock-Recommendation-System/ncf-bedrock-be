package com.example.base.domain.dto.response;

import com.example.base.domain.entity.Role;
import com.example.base.domain.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String fullName,
        String phone,
        String email,
        Role role,
        UserStatus status,
        Boolean enabled,
        LocalDateTime createdAt
) {
}
