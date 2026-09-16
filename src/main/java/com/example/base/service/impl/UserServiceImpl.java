package com.example.base.service.impl;

import com.example.base.security.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.base.constant.ErrorMessage;
import com.example.base.domain.entity.User;
import com.example.base.repository.UserRepository;
import com.example.base.service.UserService;


@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService, UserDetailsService {
  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.User.ERR_USER_NOT_EXISTED + email));
    return new CustomUserDetails(user);
  }
}
