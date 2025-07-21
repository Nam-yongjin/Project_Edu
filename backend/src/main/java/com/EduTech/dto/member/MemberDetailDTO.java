package com.EduTech.dto.member;

import java.time.LocalDate;

import com.EduTech.entity.member.MemberGender;

import lombok.Data;

@Data
public class MemberDetailDTO {

	private String memId;	// 아이디
	private String pw;		// 비밀번호
	private String name;	// 이름
	private String email;	// 이메일
	private LocalDate birthDate;	// 생년월일
	private MemberGender gender;	// 성별
	private String phone;	// 휴대폰번호
	private String addr;	// 주소
	private String addrDetail;	// 상세주소
	private boolean	checkSms;	// SMS수신동의
	private boolean checkEmail;	// 이메일수신동의
}
