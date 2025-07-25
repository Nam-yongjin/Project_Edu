package com.EduTech.dto.member;

import java.time.LocalDate;

import com.EduTech.entity.member.MemberGender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class MemberRegisterDTO {

	@NotBlank(message = "아이디는 필수입니다.")
	@Size(min = 6, max = 16, message = "6자 이상 16자 이하로 입력해주세요.")
	@Pattern(regexp = "^[A-Za-z0-9]{6,16}$", message = "영문 대소문자 또는 숫자만 입력 가능합니다.")
	private String memId;
	
	@NotBlank(message = "비밀번호는 필수입니다.")
	@Size(min = 6, max = 16, message = "6자 이상 16자 이하로 입력해주세요.")
	@Pattern(regexp = "^[A-Za-z0-9!@#$.]{6,16}$", message = "영문 대소문자, 숫자, 특수문자(!@#$.)만 사용 가능합니다.")
	private String pw;
	
	@NotBlank(message = "이름은 필수입니다.")
	@Size(max = 6, message = "6자 이하로 입력해주세요.")
	@Pattern(regexp = "^[가-힣]*$", message = "한글만 입력 가능합니다.")
	private String name;
	
	@NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
	@Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
	private String email;
	
	@NotNull(message = "생년월일은 필수입니다.")
	private LocalDate birthDate;
	
	@NotNull(message = "성별은 필수입니다.")
	private MemberGender gender;

    @NotBlank(message = "전화번호는 필수입니다.")
	@Size(min = 10, max = 11)
	@Pattern(regexp = "^01[016789][0-9]{7,8}$", message = "유효한 전화번호 형식이 아닙니다. (예: 01012345678)")
	private String phone;
	
	private String addr;
	
	private String addrDetail;
	
    @NotNull(message = "SMS 수신 동의 여부는 필수입니다.")
	private boolean	checkSms;
	
    @NotNull(message = "이메일 수신 동의 여부는 필수입니다.")
	private boolean checkEmail;
}
