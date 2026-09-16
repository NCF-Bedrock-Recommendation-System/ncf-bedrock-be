package com.example.base.controller;

import com.example.base.constant.ApiPath;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping(ApiPath.API_NOW)
@Tag(name = "User Controller", description = "API quản lý user")
public class UserController {

}
