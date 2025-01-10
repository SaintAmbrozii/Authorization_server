package com.example.authservice.config;


import com.example.authservice.secutiry.OidcUserInfoService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;

import java.util.List;

import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static org.springframework.security.oauth2.core.oidc.endpoint.OidcParameterNames.ID_TOKEN;

@Configuration
public class JwtTokenCustomizerConfig {

    @Bean
    public OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer(OidcUserInfoService userInfoService) {
        return (context) -> {
            context.getJwsHeader().type("jwt");
            if (!AuthorizationGrantType.CLIENT_CREDENTIALS.equals(context.getAuthorizationGrantType())) {
                if (ID_TOKEN.equals(context.getTokenType().getValue())) {
                    OidcUserInfo userInfo = userInfoService.getUserInfo(
                            context.getPrincipal().getName());
                    context.getClaims().claims(claims ->
                            claims.putAll(userInfo.getClaims()));
                    context.getTokenType();
                } else {
                    context.getTokenType();
                }
            }
        };
    }
}
