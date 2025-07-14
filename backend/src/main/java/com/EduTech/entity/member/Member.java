package com.EduTech.entity.member;

import java.time.LocalDate;

import com.EduTech.entity.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Member extends BaseEntity{

	@Id
	@Column(length = 16, nullable = false)
	@Size(min = 6, max = 16, message = "6자 이상 16자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9]{6,16}$", message = "영문 대소문자 또는 숫자만 입력 가능합니다.")
	private String memId;
	
	@Column(length = 16, nullable = false)
	@Size(min = 6, max = 16, message = "6자 이상 16자 이하로 입력해주세요.")
    @Pattern(regexp = "^[A-Za-z0-9!@#$.]{6,16}$", message = "영문 대소문자, 숫자, 특수문자(!@#$.)만 사용 가능합니다.")
	private String pw;
	
	@Column(length = 6, nullable = false)
	@Size(max = 6, message = "6자 이하로 입력해주세요.")
	@Pattern(regexp = "^[가-힣]*$", message = "한글만 입력 가능합니다.")
	private String name;
	
	@Column(length = 100, nullable = false, unique=true)
	private String email;
	
	@Column(nullable = false)
	private LocalDate brithDate;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberGender gender;
	
	@Column(length = 11, nullable = false, unique=true)
	@Size(min = 10, max = 11)
	@Pattern(regexp = "^01[016789][0-9]{7,8}$", message = "유효한 휴대전화 번호를 입력하세요.")
	private String phone;
	
	@Column(nullable = false)
	private String addr;
	
	@Column(nullable = false)
	private boolean	checkSms;
	
	@Column(nullable = false)
	private boolean checkEmail;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberState state;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private MemberRole role;
	
	private String kakao;
}
