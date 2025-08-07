package com.EduTech.entity.demonstration;

import java.time.LocalDate;

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
import lombok.ToString;

@Entity
@Table(name = "demonstration_reserve")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DemonstrationReserve {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long demRevNum; // 실증 신청 번호

	@Column(nullable = false)
	private LocalDate applyAt; // 신청일

	@Column(nullable = false)
	private LocalDate startDate; // 사용시작일

	@Column(nullable = false)
	private LocalDate endDate; // 사용종료일

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DemonstrationState state; // 상태

	@Column(nullable = false)
	private Long bItemNum; // 대여한 개수
	
	@ManyToOne // demonstration엔티티의 demNum 외래키
	@JoinColumn(name = "demNum")
	private Demonstration demonstration;

	@ManyToOne // member엔티티의 memId 외래키
	@JoinColumn(name = "memId")
	private Member member;
	

}
