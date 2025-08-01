package com.EduTech.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
// prePostEnabled: 메서드가 실행되기 전에 권한을 검사,  @PreAuthorize를 사용
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		
//		CORS 설정: 웹 브라우저에서 다른 출처(도메인, 포트, 프로토콜)의 리소스에 접근하려고 할 때 보안상 제한이 걸리는 것을 제어하고 허용하는 메커니즘
//		기존 cors 관련 설정은 삭제하고, 새로운 cors 관련 설정을 추가
		http.cors(httpSecurityCorsConfigurer -> {
			httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
		});
        
		// 세션을 사용하지 않도록 설정 (STATELESS)
		http.sessionManagement(sessionConfig -> {
            sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });
		
		// CSRF(사이트 간 요청 위조) 보호 기능을 비활성화, REST API에서는 일반적으로 CSRF 미사용
		http.csrf(config -> config.disable());
		
		// API 서버로 로그인
        http.formLogin(config -> {
            config.loginProcessingUrl("/api/login");
            config.successHandler(new LoginSuccessHandler());
            config.failureHandler(new LoginFailHandler());
        });
        
        // JWT 체크
        http.addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class); 
        
        // CustomSecurityConfig에 접근제한시 CustomAccessDeniedHandler를 이용하도록 설정
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });
		
        // 일단은 없어도 이미지가 잘 뜨기는 하는데 URL별로 접근 권한을 명확히 지정하기위해 사용중
        http.authorizeHttpRequests(auth -> {
            auth
            .requestMatchers("/api/event/List").permitAll()       	// 누구나 접근 가능
            .requestMatchers("/event/**").permitAll()              	// 정적 리소스 (이미지 등) 접근 허용
            .anyRequest().authenticated();                         	// 그 외는 로그인(인증) 필요
        });;
        
		return http.build();
	}
	
//	새로운 cors
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
