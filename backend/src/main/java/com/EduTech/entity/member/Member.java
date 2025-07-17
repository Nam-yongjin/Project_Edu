package com.EduTech.entity.member;

import java.time.LocalDate;

import com.EduTech.entity.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "member")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Member extends BaseEntity{

	@Id
	private String memId;
	
	private String pw;
	
	private String name;
	
	@Column(unique = true)
	private String email;
	
	private LocalDate birthDate;
	
	@Enumerated(EnumType.STRING)
	private MemberGender gender;
	
	@Column(unique = true)
	private String phone;
	
	private String addr;
	
	private String addrDetail;
	
	private boolean	checkSms;
	
	private boolean checkEmail;
	
	@Enumerated(EnumType.STRING)
	private MemberState state;
	
	@Enumerated(EnumType.STRING)
	private MemberRole role;
	
	@Column(unique=true)
	private String kakao;
	
	// 회원 삭제시 연결된 추가정보 자동 삭제
	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private Student student;
	
	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private Teacher teacher;
	
	@OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
	private Company company;
}
