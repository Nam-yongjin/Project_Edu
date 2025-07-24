package com.EduTech.dto.admin;

import java.time.LocalDateTime;

import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;

import lombok.Data;
import lombok.NoArgsConstructor;

// 관리자 회원 정보 조회 dto(백->프론트)
@Data
@NoArgsConstructor
public class AdminMemberViewResDTO {
	private String memId; // 회원 아이디
	private String name; // 회원 이름
	private String phone; // 연락처
	private String email; // 이메일
	private LocalDateTime createdAt; // 가입일
	private MemberRole role;
	private MemberState state;
	
	public AdminMemberViewResDTO(String memId, String name, String phone, String email, LocalDateTime createdAt,
			MemberRole role, MemberState state) {
		this.memId = memId;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.createdAt = createdAt;
		this.role = role;
		this.state = state;
	}
}