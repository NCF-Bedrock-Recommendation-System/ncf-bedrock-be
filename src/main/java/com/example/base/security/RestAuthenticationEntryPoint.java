package com.example.base.security;

import com.example.base.common.response.ApiResponse;
import com.example.base.constant.ErrorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest request,
                       HttpServletResponse response,
                       AuthenticationException authException) throws IOException {

    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    ApiResponse<Object> restData = ApiResponse.error(
            HttpServletResponse.SC_UNAUTHORIZED,
            ErrorMessage.UNAUTHORIZED
    );

    ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), restData);
  }
}
