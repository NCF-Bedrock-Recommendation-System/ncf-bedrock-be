package com.example.base.security;

import com.example.base.common.response.ApiResponse;
import com.example.base.constant.CommonConstant;
import com.example.base.constant.ErrorMessage;
import com.example.base.repository.InvalidatedTokenRepository;
import com.nimbusds.jwt.SignedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.text.ParseException;

@Slf4j
@Component
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  JwtProvider jwtProvider;

  UserDetailsService userDetailsService;

  InvalidatedTokenRepository invalidatedTokenRepository;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String authHeader = request.getHeader(CommonConstant.AUTHORIZATION_HEADER);

    if (authHeader == null || !authHeader.startsWith(CommonConstant.BEARER_PREFIX)) {
      log.debug("No Bearer token found in request: {}", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    String token = authHeader.substring(CommonConstant.BEARER_PREFIX.length());

    try {
      SignedJWT signedJWT = SignedJWT.parse(token);
      String jwtId = signedJWT.getJWTClaimsSet().getJWTID();

      if (invalidatedTokenRepository.existsById(jwtId)) {
        sendErrorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, ErrorMessage.Auth.ERR_TOKEN_INVALIDATED);
        return;
      }
    } catch (ParseException e) {
        sendErrorResponse(response, HttpServletResponse.SC_BAD_REQUEST, ErrorMessage.Auth.ERR_MALFORMED_TOKEN);
      return;
    }

    String email = jwtProvider.extractEmail(token);

    if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      UserDetails userDetails = userDetailsService.loadUserByUsername(email);

      if (jwtProvider.isTokenValid(token, userDetails)) {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
            null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);
      } else {
        log.warn("Invalid token for user: {}", email);
      }
    }

    filterChain.doFilter(request, response);
  }

  private void sendErrorResponse(HttpServletResponse response, int status, String message) throws IOException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(status);

    ApiResponse<Object> restData = ApiResponse.error(status, message);

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), restData);
  }
}
