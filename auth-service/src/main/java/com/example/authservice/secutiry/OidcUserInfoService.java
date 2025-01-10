package com.example.authservice.secutiry;


import com.example.authservice.domain.User;
import com.example.authservice.service.CustomUserDetailService;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.stereotype.Service;

@Service
public class OidcUserInfoService {

    private final CustomUserDetailService userDetailService;

    public OidcUserInfoService(CustomUserDetailService userDetailService) {
        this.userDetailService = userDetailService;
    }

    public OidcUserInfo getUserInfo(String username) {
        User user = userDetailService.loadUserByUsername(username);
        return OidcUserInfo.builder()
                .subject(user.getId().toString())
                .name(user.getName())
                .givenName(user.getLastname())
                .familyName(user.getSurname())
                .nickname(username)
                .preferredUsername(username)
                .profile("https://example.com/" + username)
                .website("https://example.com")
                .email(user.getEmail())
                .emailVerified(true)
                .claim("roles", user.getAuthorities())
                .zoneinfo("Europe/Berlin")
                .locale("de-DE")
                .updatedAt("1970-01-01T00:00:00Z")
                .build();
    }
}
