package com.EduTech.entity.facility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor  //기본 생성자 추가
@AllArgsConstructor
@Entity
@Table(name = "facility_reserve")
@Builder
@Getter
@Setter
public class FacilityReserve {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facRevNum;			//예약 번호 

    private LocalDate facDate;		// 예약날자

    private LocalTime startTime;	// 시작시간

    private LocalTime endTime;		// 종료시간

    @Column(nullable = false)
    private LocalDateTime reserveAt;	// 예약신청일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityState state = FacilityState.WAITING; // 기본값: 대기

    @ManyToOne
    @JoinColumn(name = "facility_num")
    private Facility facility;		// 

    @Column(nullable = false, length = 30)
    private String memId; 			// 실제 시스템에서는 Member 객체로 연결 가능
}