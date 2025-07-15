package com.EduTech.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.EduTech.security.CustomAccessDeniedHandler;
import com.EduTech.security.LoginFailHandler;
import com.EduTech.security.LoginSuccessHandler;
import com.EduTech.security.jwt.JWTFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
//		CORS 설정: 웹 브라우저에서 다른 출처(도메인, 포트, 프로토콜)의 리소스에 접근하려고 할 때 보안상 제한이 걸리는 것을 제어하고 허용하는 메커니즘
		http.cors(httpSecurityCorsConfigurer -> {
			httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
		});

		// 세션을 사용하지 않도록 설정 (STATELESS)
		http.sessionManagement(sessionConfig -> {
            sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
		
		// CSRF(사이트 간 요청 위조) 보호 기능을 비활성화, REST API에서는 일반적으로 CSRF 미사용
		http.csrf(config -> config.disable());
		
		// api서버 로그인
        http.formLogin(config -> {
            config.loginPage(("/api/member/login"));
            config.successHandler(new LoginSuccessHandler());
            config.failureHandler(new LoginFailHandler());
        });
        
        // JWT 체크
        http.addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class); 
        
        // CustomSecurityConfig에 접근제한시 CustomAccessDeniedHandler를 이용하도록 설정
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });
		
		return http.build();
	}
	
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOriginPatterns(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);

		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
