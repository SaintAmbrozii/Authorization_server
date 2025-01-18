package com.example.messageservice.controller;

import com.example.messageservice.userinfo.UserInfo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Optional;

@RestController
public class ResourceController {


    @GetMapping("/messages")
    public String getProtectedResource(@AuthenticationPrincipal Jwt jwt) {

        return "Привет!" + String.valueOf(jwt.getTokenValue());
    }
}
