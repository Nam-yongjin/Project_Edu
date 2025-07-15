package com.EduTech.dto.member;

import java.time.LocalDate;

import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;

import lombok.Data;

@Data
public class ModMemberDTO {

	private String memId;
	private String pw;
	private String name;
	private String email;
	private LocalDate birthDate;
	private MemberGender gender;
	private String phone;
	private String addr;
	private boolean	checkSms;
	private boolean checkEmail;
}
