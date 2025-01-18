package com.example.resourceserver.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
public class ResourceController {


 //   @GetMapping("/resource")
 //   public String getProtectedResource(@AuthenticationPrincipal Jwt jwt) {

 //      return "Привет!" + jwt.getClaims();
 //   }

    @GetMapping("resource")
    public Map<String, Object> getUserInfo(@AuthenticationPrincipal Jwt jwt) {

        return jwt.getClaims();
    }
}
