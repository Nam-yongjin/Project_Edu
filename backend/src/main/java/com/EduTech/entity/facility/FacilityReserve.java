package com.EduTech.entity.facility;

import java.time.LocalDateTime;

import com.EduTech.entity.member.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facility_reserve")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FacilityReserve {

	@Id
	@Column(nullable = false) //예약신청번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long facRevNum;
	
	@Column(nullable = false) //신청일
	private LocalDateTime reserveAt;
	
	@Column(nullable = false) //이용시작일
	private LocalDateTime startDate;
	
	@Column(nullable = false) //이용종료일
	private LocalDateTime endDate;
	
	@Enumerated(EnumType.STRING) //상태
	@Column(nullable = false)
	private FacilityState state;
	
	@ManyToOne(fetch = FetchType.LAZY) //하나의 시설에 여러 개의 예약 가능
	@JoinColumn(name = "facilityNum", nullable = false) //장소번호
	private Facility facility;
	
	@ManyToOne(fetch = FetchType.LAZY) //하나의 시설에 여러 명의 회원이 예약 가능
	@JoinColumn(name = "memId", nullable = false) //회원아이디
	private Member member;
	
	
}
