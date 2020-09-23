package com.david.SecurityApi.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;

@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {

    private static final String RESOURCE_ID = "security-api";

    private static final String[] AUTH_WHITELIST = {
            // -- swagger ui urls
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**"
    };

    @Override
    public void configure (ResourceServerSecurityConfigurer resources){
        resources.resourceId(RESOURCE_ID).stateless(false);
    }

    @Override
    public void configure (HttpSecurity http) throws Exception{
        http.headers()
                .frameOptions()
                .disable()
                .and()
                .authorizeRequests()
                .antMatchers("/", "/v1/user/createUser", "/swagger-ui.html", "/webjars/**", "/swagger-resources","/swagger-resources/**", "/configuration/ui", "/configuration/security", "/v2/api-docs", "/oauth/check_token").permitAll()
                .and().authorizeRequests().anyRequest().authenticated();
    }
}