package com.EduTech.dto.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;

// User: Spring Security가 기본 제공하는 UserDetails 구현체, 인증된 사용자 정보 담는 객체이므로 MemberDTO에서만 사용
@Data
public class MemberDTO extends User {

	// 인증 및 권한에 필요한 최소한의 정보
	private String memId;
	
	private String pw;

	private String email;
	
	private String state;

	private String role;

	// 생성자
	public MemberDTO(String memId, String pw, String email, String state, String role) {
		super(memId, pw, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
		this.memId = memId;
		this.email = email;
		this.state = state;
		this.role = role;
	}
	
	// JWT Claim 변환
	// JWT 안에 사용자 고유 정보(memId, role, state)를 담아서, 이후 인증 없이도 유저 정보를 식별 가능
	public Map<String, Object> getClaims() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("memId", memId);
		dataMap.put("email", email);
		dataMap.put("state", state);
		dataMap.put("role", role);
		return dataMap;
	}

}
