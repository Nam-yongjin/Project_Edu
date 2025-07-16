package com.EduTech.dto.member;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.EduTech.entity.member.MemberRole;

import lombok.Data;

@Data
public class MemberDTO extends User { // User은 인증된 사용자 정보 담는 객체이므로 MemberDTO에서만 사용

	private String memId;

	private String pw;

	private String name;

	private MemberRole role;

	// 생성자
	public MemberDTO(String memId, String pw, String name, MemberRole role) {
		super(memId, pw, List.of(new SimpleGrantedAuthority("ROLE_" + role)));
		this.memId = memId;
		this.pw = pw;
		this.name = name;
		this.role = role;
	}

	// JWT 등을 쓸 때 사용자의 정보를 Map으로 추출
	public Map<String, Object> getClaims() {
		Map<String, Object> dataMap = new HashMap<>();
		dataMap.put("memId", memId);
		dataMap.put("name", name);
		dataMap.put("role", role);

		return dataMap;
	}

}
