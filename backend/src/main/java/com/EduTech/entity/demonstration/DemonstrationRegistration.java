package com.EduTech.entity.demonstration;

import java.time.LocalDateTime;
<<<<<<< HEAD
=======
import java.util.Date;
>>>>>>> refs/heads/demonstration

import com.EduTech.entity.member.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "demonstration_registration")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DemonstrationRegistration {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long demRegNum; // 실증 등록 번호
	
	@Column(nullable = false)
	private LocalDateTime regDate; // 등록일
	
	
	@Column(nullable=false)
	private LocalDateTime expDate;
	
	@Enumerated(EnumType.STRING)
	private DemonstrationState state; // 상태
	
	
	@ManyToOne // demonstration엔티티의 demNum 외래키
	@JoinColumn(name = "demNum") 
	private Demonstration demonstration;
	
	
	@ManyToOne // member엔티티의 memId 외래키
	@JoinColumn(name = "memId") 
	private Member member;
}
