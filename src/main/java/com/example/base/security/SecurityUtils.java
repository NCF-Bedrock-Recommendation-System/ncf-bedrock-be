package com.example.base.security;

import com.example.base.constant.ErrorMessage;
import com.example.base.exception.nonRetryException.UnauthorizedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class SecurityUtils {
  public static UUID getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
      throw new UnauthorizedException(ErrorMessage.UNAUTHORIZED);
    }

    Object principal = authentication.getPrincipal();

    if (principal instanceof CustomUserDetails) {
      return ((CustomUserDetails) principal).getUser().getId();
    }

    if (principal instanceof String && principal.equals("anonymousUser")) {
      throw new UnauthorizedException(ErrorMessage.UNAUTHORIZED);
    }

    throw new UnauthorizedException(ErrorMessage.UNAUTHORIZED);
  }

  public static String getCurrentUsername() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null)
      return null;
    return authentication.getName();
  }
}
