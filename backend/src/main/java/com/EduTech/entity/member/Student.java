package com.EduTech.entity.member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;

@Entity
@Table(name = "student")
@Builder
@Data
public class Student {
	
	@Column(nullable = false) //학교
	private String schoolName;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
}
