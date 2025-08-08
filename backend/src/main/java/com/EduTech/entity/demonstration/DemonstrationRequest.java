package com.EduTech.entity.demonstration;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
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
	private Long demRevNum;

	@ManyToOne
	@MapsId //reserve를 기본 키와 매핑
	@JoinColumn(name = "demRevNum")
	private DemonstrationReserve reserve;
	private DemonstrationState state;
	private RequestType type;
	private LocalDate applyAt; 
}
