package org.twitter.api.common.config;
import static org.springframework.security.config.Customizer.withDefaults;

import org.twitter.api.common.filter.HttpLoggingFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final HttpLoggingFilter HttpLoggingFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 헤더 보안 설정
        http
            .headers((headers) ->
                headers
                    .contentTypeOptions(withDefaults())
                    .xssProtection(withDefaults())
                    .cacheControl(withDefaults())
                    .httpStrictTransportSecurity(withDefaults())
                    .frameOptions(withDefaults())
            );

        // csrf 비활성, cors 비활성
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable);

        // form 로그인 비활성, httpBasic 비활성
        http
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        // 세션 설정
        http
            .sessionManagement((sessionManagement) ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );


        // http 로깅 필터
        http
            .addFilterBefore(HttpLoggingFilter, LogoutFilter.class);

        // 로그아웃 설정

        // 인증 설정

        // 예외 처리 설정

        // 인가 설정
        http
            .authorizeHttpRequests((authorizeRequests) ->
                    authorizeRequests
                        .anyRequest().permitAll()
            );

        return http.build();
    }
}
