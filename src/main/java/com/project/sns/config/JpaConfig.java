package com.project.sns.config;

import com.project.sns.dto.security.BoardPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@EnableJpaAuditing // @EnableJpaAuditing: JPA Auditing(감사 기능)을 활성화 -> 엔티티가 생성되거나 수정될 때, 자동으로 날짜와 시간을 기록해준다.
@Configuration
public class JpaConfig {
    @Bean
    public AuditorAware<String> auditorAware() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext())
                .map(SecurityContext::getAuthentication)
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .map(BoardPrincipal.class::cast) // .map(x -> (BoardPrincipal)x)
                .map(BoardPrincipal::getUsername);
    }
}
