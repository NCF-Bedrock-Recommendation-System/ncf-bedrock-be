package com.example.base.domain.mapper;

import com.example.base.domain.dto.response.UserResponseDto;
import com.example.base.domain.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

  public UserResponseDto from(User user) {
    return new UserResponseDto(
            user.getId(),
            user.getFullName(),
            user.getPhone(),
            user.getEmail(),
            user.getRole(),
            user.getEnabled(),
            user.getCreatedAt()
    );
  }
}
