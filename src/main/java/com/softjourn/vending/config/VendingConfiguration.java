package com.softjourn.vending.config;

import com.softjourn.common.auth.OAuthHelper;
import com.softjourn.vending.dao.RefreshableRepositoryImpl;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.UrlResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableJpaRepositories(basePackages = "com.softjourn.vending.dao", repositoryBaseClass = RefreshableRepositoryImpl.class)
@EnableTransactionManagement
public class VendingConfiguration extends ResourceServerConfigurerAdapter {

    @Value("${auth.client.id}")
    private String clientId;

    @Value("${authPublicKeyFile}")
    private String authPublicKeyFile;

    @Value("${auth.server.host}")
    private String authServerHost;

    @Value("${auth.client.secret}")
    private String clientSecret;

    @Bean
    public OAuthHelper oAuthHelper() {
        return new OAuthHelper(clientId, clientSecret, authServerHost, new RestTemplate());
    }

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
        try (InputStream inputStream = new UrlResource("file:" + authPublicKeyFile).getInputStream()) {
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
                .antMatchers(HttpMethod.POST, "/v1/vending/**", "/v1/products/**").authenticated()
                .antMatchers(HttpMethod.DELETE, "/v1/vending/**", "/v1/products/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.NEVER)
                .and()
                .csrf()
                .disable();
    }

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }

}
