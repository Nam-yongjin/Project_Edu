package com.EduTech.dto.member;

import java.time.LocalDate;

import com.EduTech.entity.member.MemberGender;

import lombok.Data;

@Data
public class MemberModifyDTO {

	private String memId;
	private String pw;
	private String name;
	private String email;
	private LocalDate birthDate;
	private MemberGender gender;
	private String phone;
	private String addr;
	private String addrDetail;
	private boolean	checkSms;
	private boolean checkEmail;
}
