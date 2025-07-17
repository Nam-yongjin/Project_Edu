package com.EduTech.entity.member;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "company")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Company {
	
	@Id
    private String memId;  // Member의 PK와 동일하게 사용
	
	private String companyName;
	
	private String position;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
	
}
