package com.EduTech.security.jwt;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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

			String accessToken = authHeader.substring(7);

			Map<String, Object> claims = JWTProvider.validateToken(accessToken);

			String memId = (String) claims.get("memId");
			String pw = (String) claims.get("pw");
			String name = (String) claims.get("name");
			List<String> roleNames = (List<String>) claims.get("roleNames");

			MemberDTO memberDTO = new MemberDTO(memId, pw, name, roleNames);

			UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDTO,
					null, memberDTO.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			filterChain.doFilter(request, response);

		} catch (Exception e) {
			Gson gson = new Gson();
			String json = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));

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

	public static List<String> getRoleNames() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Object principal = authentication.getPrincipal();
		if (principal == null || !(principal instanceof MemberDTO)) {
			return List.of();
		}

		Collection<? extends GrantedAuthority> authorities = ((MemberDTO) principal).getAuthorities();

		return authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList());
	}

	// 특정 Role이 있는지 검사
	public static boolean hasRole(String role) {
		return getRoleNames().contains("ROLE_" + role.toUpperCase());
	}
}
