package com.EduTech.dto.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Data;

@Data
public class MemberDTO extends User { // User은 인증된 사용자 정보 담는 객체이므로 MemberDTO에서만 사용

	// 인증 및 권한에 필요한 최소한의 정보
	private String memId;

	private String pw;

	private String name;

	private String roleName;

	// 생성자
	public MemberDTO(String memId, String pw, String name, String roleName) {
		super(memId, pw, List.of(new SimpleGrantedAuthority("ROLE_" + roleName)));
		this.memId = memId;
		this.name = name;
		this.roleName = roleName;
	}
	
	// JWT 등을 쓸 때 사용자의 정보를 Map으로 추출
	public Map<String, Object> getClaims() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("memId", memId);
		dataMap.put("name", name);
		dataMap.put("roleName", roleName);
		return dataMap;
	}

}
