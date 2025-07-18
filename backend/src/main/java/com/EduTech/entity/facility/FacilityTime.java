package com.EduTech.entity.facility;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
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
@Table(name = "facility_time")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FacilityTime {

	@Id
	@Column(nullable = false) //시간번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long facTimeNum;
	
	@Column(nullable = false) //날짜
	private LocalDate facDate;
	
	@Column(nullable = false) //시작시간
	private LocalTime startTime;
	
	@Column(nullable = false) //종료시간
	private LocalTime endTime;
	
	@Enumerated(EnumType.STRING) //예약상태
	@Column(nullable = false)
	private FacilityState state;
	
	@ManyToOne(fetch = FetchType.LAZY) //하나의 시설에 여러 개의 시간 예약 가능
	@JoinColumn(name = "facilityNum", nullable = false) //장소번호
	private Facility facility;
	
}
