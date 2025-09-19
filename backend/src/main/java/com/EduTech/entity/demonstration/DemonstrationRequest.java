package com.EduTech.entity.demonstration;

import java.time.LocalDate;

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
@Table(name = "demonstration_request")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DemonstrationRequest { // res 반납 요청 및 기한 연장을 위한 엔티티

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long requestId; // 요청 번호

	@ManyToOne
	@JoinColumn(name = "demRevNum")
	private DemonstrationReserve reserve; // 대여 정보

	@Enumerated(EnumType.STRING)
	private DemonstrationState state; // 상태값

	@Enumerated(EnumType.STRING)
	private RequestType type; // 반납 / 연장 여부
 
	private LocalDate applyAt; // 신청일
	
	private LocalDate updateDate; // 변경 날짜

}
