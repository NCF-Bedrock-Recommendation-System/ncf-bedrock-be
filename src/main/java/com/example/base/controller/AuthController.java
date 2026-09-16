package com.example.base.controller;

import com.example.base.constant.ApiPath;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(ApiPath.API_NOW + "/auth")
@Tag(name = "Auth Controller", description = "Đăng ký, đăng nhập, đăng xuất và refresh token")
public class AuthController {
}
