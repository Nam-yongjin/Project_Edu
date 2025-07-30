package com.EduTech.security.jwt;

import java.io.IOException;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.EduTech.dto.member.MemberDTO;
import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JWTFilter extends OncePerRequestFilter {

	// JWT를 검증하고, 인증 정보를 SecurityContext에 등록
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		try {
			String authHeader = request.getHeader("Authorization");
			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			    filterChain.doFilter(request, response);
			    return;  // 헤더 없거나 형식 안 맞으면 인증 안하고 다음 필터 실행
			}
			String accessToken = authHeader.substring(7);

			Map<String, Object> claims = JWTProvider.validateToken(accessToken);

			String memId = (String) claims.get("memId");
			String email = (String) claims.get("email");
			String state = (String) claims.get("state");
			String role = (String) claims.get("role");

			MemberDTO memberDTO = new MemberDTO(memId, "", email, state, role);

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDTO,
					null, memberDTO.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			String json = new Gson().toJson(Map.of("error", "ERROR_ACCESS_TOKEN", "message", e.getMessage()));
			response.setContentType("application/json;charset=UTF-8");
			response.getWriter().println(json);

		}
	}

	// 필터로 체크하지 않을 경로나 메서드등을 지정
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {

		String path = request.getRequestURI();

		// 회원가입 경로 제외
		if (path.startsWith("/register") && request.getMethod().equals("POST")) {
			return true;
		}
		// 아이디 중복 체크 경로 제외
		if (path.startsWith("/checkId") && request.getMethod().equals("GET")) {
			return true;
		}
		// 아이디 찾기크 경로 제외
		if (path.startsWith("/findId") && request.getMethod().equals("GET")) {
			return true;
		}
		// 비밀번호 찾기(변경) 경로 제외
		if (path.startsWith("/resetPw") && request.getMethod().equals("PUT")) {
			return true;
		}

		// 이미지 조회 경로는 제외 (/view)
		if (path.matches("^.*/view(/.*)?$")) {
			return true;
		}
		return false;

	}

	public static String getMemId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal == null || !(principal instanceof MemberDTO)) {
			return null;
		}

		return ((MemberDTO) principal).getUsername();
	}

}
