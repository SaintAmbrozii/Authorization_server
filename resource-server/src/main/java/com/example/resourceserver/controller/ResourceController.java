package com.example.resourceserver.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class ResourceController {


    @GetMapping("/resource")
    public String getProtectedResource(@AuthenticationPrincipal Jwt jwt) {

        return "Привет , "  + "!" + jwt.getSubject();
    }
}
