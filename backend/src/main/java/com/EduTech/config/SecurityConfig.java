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
		
		// (추가) preflight(OPTIONS) 는 인증 없이 통과
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .anyRequest().permitAll()
        );
		
		// API 서버로 로그인
        // UsernamePasswordAuthenticationFilter가 /api/login 요청을 가로챔
        // username, password 파라미터를 꺼내 UsernamePasswordAuthenticationToken 객체를 생성
        // 토큰을 AuthenticationManager에 전달 → 내부적으로 AuthenticationProvider(보통 DaoAuthenticationProvider) 실행
        // 이때 UserDetailsService.loadUserByUsername(username) 호출 -> MemberDTO을 반환
        // DaoAuthenticationProvider가 반환된 MemberDTO.getPassword() 와 요청에서 넘어온 password를 PasswordEncoder.matches()로 비교.
        // 성공 시: SecurityContextHolder에 Authentication 저장
        // AuthenticationSuccessHandler 실행 → 여기서 LoginSuccessHandler 실행
        // 실패 시: AuthenticationFailureHandler 실행 → 여기서 LoginFailHandler 실행
        http.formLogin(config -> {
            config.loginProcessingUrl("/api/login"); // /api/login으로 들어오는 요청만 가로채서 인증 처리
            config.successHandler(new LoginSuccessHandler()); // 로그인 성공시
            config.failureHandler(new LoginFailHandler());	// 로그인 실패시
        });
        
        // JWT 체크
        http.addFilterBefore(new JWTFilter(), UsernamePasswordAuthenticationFilter.class); 
        
        // CustomSecurityConfig에 접근제한시 CustomAccessDeniedHandler를 이용하도록 설정
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });
        
		return http.build();
	}
	
//	새로운 cors
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration configuration = new CorsConfiguration();

		configuration.setAllowedOriginPatterns(Arrays.asList("*"));
		configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
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
