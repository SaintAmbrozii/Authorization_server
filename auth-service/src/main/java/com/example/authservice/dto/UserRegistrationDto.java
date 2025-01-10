package com.example.authservice.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class UserRegistrationDto {

    private String name;
    private String lastname;
    private String surname;
    private String address;
    private String email;
    private String password;

}
