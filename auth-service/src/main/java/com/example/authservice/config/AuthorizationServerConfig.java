package com.example.authservice.config;



import com.example.authservice.jose.Jwks;
import com.example.authservice.properties.AuthorizationServerProperties;
import com.example.authservice.secutiry.OidcUserInfoService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationContext;
import org.springframework.security.oauth2.server.authorization.oidc.authentication.OidcUserInfoAuthenticationToken;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration(proxyBeanMethods = false)
public class AuthorizationServerConfig {


    private final OidcUserInfoService userInfoService;
    private final AuthorizationServerProperties properties;
    private final PasswordEncoder encoder;
    private static final String CLIENT_SECRET = "secret";

    public AuthorizationServerConfig(OidcUserInfoService userInfoService, AuthorizationServerProperties properties, PasswordEncoder encoder) {
        this.userInfoService = userInfoService;
        this.properties = properties;
        this.encoder = encoder;
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public SecurityFilterChain authServerSecurityFilterChain(HttpSecurity http) throws Exception {


        Function<OidcUserInfoAuthenticationContext, OidcUserInfo> userInfoMapper = (context) -> {
            OidcUserInfoAuthenticationToken authentication = context.getAuthentication();
            return new OidcUserInfo(userInfoService.getUserInfo(authentication.getName()).getClaims());
        };

        OAuth2AuthorizationServerConfigurer authorizationServerConfigurer = new OAuth2AuthorizationServerConfigurer();

        RequestMatcher endpointsMatcher = authorizationServerConfigurer.getEndpointsMatcher();
        http.securityMatcher(endpointsMatcher).authorizeHttpRequests((authorize) ->
                authorize.anyRequest().authenticated())
                .exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
                .csrf((csrf) -> {
            csrf.ignoringRequestMatchers(endpointsMatcher);
        }).
                with(authorizationServerConfigurer, withDefaults());
        authorizationServerConfigurer.oidc(
                o -> o
                        .providerConfigurationEndpoint(withDefaults())
                        .clientRegistrationEndpoint(withDefaults())
                        .userInfoEndpoint((userInfo) -> userInfo
                                .userInfoMapper(userInfoMapper)
                        )
        );

        http.oauth2ResourceServer(oauth2->oauth2.jwt(Customizer.withDefaults()));



        return http.build();

 //       OAuth2AuthorizationServerConfigurer authorizationServerConfigurer =
 //               OAuth2AuthorizationServerConfigurer.authorizationServer().oidc(Customizer.withDefaults()); // Enable OpenID Connect 1.0
 //       http
 //               .securityMatcher(authorizationServerConfigurer.getEndpointsMatcher())
 //               .with(authorizationServerConfigurer, Customizer.withDefaults())
 //               .authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
 //               .exceptionHandling((exceptions) -> exceptions.defaultAuthenticationEntryPointFor(
  //                      new LoginUrlAuthenticationEntryPoint("/login"), new MediaTypeRequestMatcher(MediaType.TEXT_HTML)))
   //             .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));

    //    return http.build();

    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {

        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientId("gateway")
                .clientSecret(encoder.encode(CLIENT_SECRET))
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://127.0.0.1:8080/login/oauth2/code/gateway")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope("message.read")
                .scope("message.write")
                .build();


        return new InMemoryRegisteredClientRepository(registeredClient);
    }


    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings
                .builder()
                .issuer("http://localhost:9000")
                .build();
    }



    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        RSAKey rsaKey = Jwks.generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }



    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }





}