package com.EduTech.security;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.EduTech.dto.member.MemberDTO;
import com.EduTech.security.jwt.JWTProvider;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginSuccessHandler implements AuthenticationSuccessHandler { // 인증 성공시 콜백

	// 로그인 성공 시 자동으로 호출되는 메서드
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();
		Map<String, Object> claims = memberDTO.getClaims();

		String accessToken = JWTProvider.generateToken(claims, 10);	// accessToken 유효시간 10분
		String refreshToken = JWTProvider.generateToken(claims, 60 * 24); // refreshToken 유효시간 1일
		
		claims.put("accessToken", accessToken);
        claims.put("refreshToken", refreshToken);

        // JSON 형태로 반환
        Gson gson = new Gson();
        String jsonStr = gson.toJson(claims);
        response.setContentType("application/json");

        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
	}
}
