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
@Table(name = "teacher")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Teacher {
	
	@Id
    private String memId;  // Member의 PK와 동일하게 사용
	
	private String schoolName;
	
	@OneToOne
	@MapsId
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;

}
