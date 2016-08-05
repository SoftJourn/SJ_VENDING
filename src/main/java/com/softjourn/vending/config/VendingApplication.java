package com.softjourn.vending.config;


import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.io.IOException;
import java.io.InputStream;


@SpringBootApplication
@ComponentScan(basePackages = "com.softjourn.vending")
@EnableResourceServer
@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableJpaRepositories(basePackages = "com.softjourn.vending.dao")
@EntityScan(basePackages = "com.softjourn.vending.entity")
public class VendingApplication extends ResourceServerConfigurerAdapter {

    @Value("${auth.server.host}")
    private String authServerHost;

    @Value("${auth.redirect.url}")
    private String authRedirectUri;

    @Value("${auth.client.id}")
    private String clientId;

    public static void main(String[] args) {
        SpringApplication.run(VendingApplication.class, args);
    }

    @Value("${authPublicKeyFile}")
    private String authPublicKeyFile;

    @Bean
    public TokenStore tokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        String publicKey = readPublicKey(authPublicKeyFile);
        converter.setVerifierKey(publicKey);
        return converter;
    }

    @Bean
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        return defaultTokenServices;
    }

    private String readPublicKey(String authPublicKeyFile) {
        try (InputStream inputStream = new ClassPathResource(authPublicKeyFile).getInputStream()) {
            return IOUtils.toString(inputStream, "utf8");
        } catch (IOException e) {
            throw new RuntimeException("Can't read auth public key from file " + authPublicKeyFile);
        }
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/admin").authenticated()
                .anyRequest().permitAll()
            .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
            .and()
            .csrf()
                .disable()
            .exceptionHandling()
                .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> httpServletResponse
                        .sendRedirect(buildAuthRedirectURI())
        );
    }

    private String buildAuthRedirectURI() {
        return authServerHost +
                "/oauth/authorize?" +
                "response_type=code&" +
                "redirect_uri="+ authRedirectUri +"&" +
                "response_type=code&" +
                "scope=read&" +
                "client_id=" + clientId;
    }

    public static class ServletInit extends SpringBootServletInitializer {

        @Override
        protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
            return application.sources(VendingApplication.class);
        }
    }
}
