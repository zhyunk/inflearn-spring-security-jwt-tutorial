package com.inflearn.project.config;

import com.inflearn.project.jwt.JwtAccessDeniedHandler;
import com.inflearn.project.jwt.JwtAuthenticationEntryPoint;
import com.inflearn.project.jwt.JwtFilter;
import com.inflearn.project.jwt.JwtSecurityConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@RequiredArgsConstructor
@EnableMethodSecurity
@Configuration
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final JwtFilter jwtFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcRequest = new MvcRequestMatcher.Builder(introspector);
        http
                .csrf(AbstractHttpConfigurer::disable) // token 사용을 위함
                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class) // cors 설정

                .exceptionHandling(config -> config
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler))
                .sessionManagement(config -> config.sessionCreationPolicy(STATELESS)) // session 미사용 설정
                .headers(config -> config.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)) // h2를 위한 설정

                .authorizeHttpRequests(config -> config
                        .requestMatchers(PathRequest.toH2Console()).permitAll()
                        .requestMatchers(
                                mvcRequest.pattern("/api/hello"),
                                mvcRequest.pattern("/api/authenticate"),
                                mvcRequest.pattern("/api/signup")
                        ).permitAll()
                        .anyRequest().authenticated())

                .apply(new JwtSecurityConfig(jwtFilter));

        return http.build();
    }

}
