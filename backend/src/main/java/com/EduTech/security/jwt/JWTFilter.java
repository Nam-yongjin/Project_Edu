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
			
			// Authorization 헤더가 없거나 "Bearer "로 시작하지 않을 경우, JWT 검증을 생략하고 필터를 통과시키기 위해 사용중
			// 삭제시 eventList 썸네일 안나옴
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            filterChain.doFilter(request, response);
	            return;
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
	    String method = request.getMethod();
	    String authHeader = request.getHeader("Authorization");

	    // ✅ 정적 경로 or 이미지 조회
	    if (path.equals("/favicon.ico") || path.matches("^.*/view(/.*)?$")) return true;

	    // ✅ 명시적 인증 제외 API
	    if (isExcludedPath(path, method)) return true;

	    // ✅ /api 경로 중 토큰 없는 경우 비회원 → 필터 적용 안 함
	    if (path.startsWith("/api")) {
	        return authHeader == null;
	    }

	    return false;
	}

	// 인증 없이 접근 허용할 경로들 정의
	private boolean isExcludedPath(String path, String method) {
	    return
	        (path.startsWith("/api/register") && method.equals("POST")) ||
	        (path.startsWith("/api/checkId") && method.equals("GET")) ||
	        (path.startsWith("/api/findId") && method.equals("GET")) ||
	        (path.startsWith("/api/resetPw") && method.equals("PUT")) ||
	        (path.startsWith("/api/refresh"));
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
