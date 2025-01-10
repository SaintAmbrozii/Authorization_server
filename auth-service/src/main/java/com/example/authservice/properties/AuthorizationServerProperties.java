package com.example.authservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.resourceserver.opaquetoken")
public class AuthorizationServerProperties {

    private String issuerUrl;
    private String introspectionUri;
}
