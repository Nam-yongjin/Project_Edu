package com.EduTech.entity.member;

import jakarta.persistence.*;

public class Teacher {
	
	@Column(nullable = false) //학교
	private String schoolName;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;

}
