package com.example.authservice.controller;


import com.example.authservice.domain.User;
import com.example.authservice.dto.UserRegistrationDto;
import com.example.authservice.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("register")
    public User createUser(@RequestBody UserRegistrationDto userdto) {
        return userService.createUser(userdto);
    }

}
