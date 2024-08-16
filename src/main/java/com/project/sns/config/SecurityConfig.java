package com.project.sns.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

/*
Spring Security 에서는 인증,인가에 대한 처리를 여러개의 필터를 통해 연쇄적으로 실행하여 수행한다.
Security Filter Chain 빈을 생성하는 securityFilterChain 메소드가 Security Filter Chain 에 대한 전반적인 설정을 구성하며
해당 메소드에 매개변수인 HttpSecurity 가 설정을 기반으로 Security Filter Chain 을 생성한다.

 */
@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .formLogin(withDefaults())
                .build();
    }
}
