package com.EduTech.entity.member;

import jakarta.persistence.*;

public class Company {
	
	@Column(nullable = false) //기업명
	private String companyName;
	
	@Column(nullable = false) //직급
	private String position;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "memId") //회원아이디
	private Member member;
	
}
