package com.EduTech.dto.admin;

import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;

import lombok.Data;
import lombok.NoArgsConstructor;

// 관리자 회원 정보 페이지에서 필터링 처리위해
// 값 받아오는 dto (프론트->백)
// 실제 정보가 아니라 어떤 필드가 변햇는지를 알려줌
// 바뀐 값만 값 넣어서 옴.
@Data
@NoArgsConstructor
public class AdminMemberViewReqDTO {
	private String memId; // 회원 아이디
	private String name; // 회원 이름
	private String email; // 이메일
	private String phone; // 휴대폰
	private MemberRole role;
	private MemberState state;

	private String sortField; // 정렬 기준
	private String sortDirection; // 정렬 방향

	public AdminMemberViewReqDTO(String memId, String name, String email, String phone, MemberRole role, MemberState state) {
		this.memId = memId;
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.role = role;
		this.state = state;
	}
}
