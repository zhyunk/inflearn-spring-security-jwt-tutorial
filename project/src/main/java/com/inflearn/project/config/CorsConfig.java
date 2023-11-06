package com.inflearn.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 인증 정보를 요청에 포함할 수 있도록 허용
        config.addAllowedOriginPattern("*"); // 모든 도메인에서의 요청을 허용
        config.addAllowedHeader("*");
        config.addAllowedMethod("*"); // 모든 HTTP 메서드를 허용

        source.registerCorsConfiguration("/api/**", config); // `/api/**` 패턴으로 들어오는 요청에 대한 설정 적용
        return new CorsFilter(source);
    }
}
