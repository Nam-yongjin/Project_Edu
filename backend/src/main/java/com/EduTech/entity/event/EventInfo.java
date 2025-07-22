package com.EduTech.entity.event;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventNum; // 프로그램번호

	@Column(nullable = false, length = 200)
	private String eventName; // 프로그램명

	@Column(nullable = false, columnDefinition = "TEXT")
	private String eventInfo; // 프로그램 상세 내용
	
	@Column(nullable = false)
	private LocalDateTime applyStartPeriod; // 신청시작기간

	@Column(nullable = false)
	private LocalDateTime applyEndPeriod; // 신청종료기간

	@Column(nullable = false, length = 20) // 신청전 / 신청중 / 신청마감 등
	private String status;

/*		필요 없을것으로 예상중
	@Builder.Default
	@ElementCollection
	@CollectionTable(name = "event_days", joinColumns = @JoinColumn(name = "eventNum"))
	@Column(nullable = false)
	private List<Integer> daysOfWeek  = new ArrayList<>();
*/
	
	@Column(nullable = false, length = 20)
	private String place; // 장소

	@Column(nullable = false)
	private LocalDate eventStartPeriod; // 행사시작기간

	@Column(nullable = false)
	private LocalDate eventEndPeriod; // 행사종료기간

	@Column(nullable = false, length = 20)
	private String target; // 수강대상

	@Column(nullable = false)
	private int maxCapacity; // 모집인원

	@Column(length = 100)
	private String originalName; // 파일명

	@Column(length = 200)
	private String filePath; 	// 파일경로
	
	@Column(length = 200)
	private String fileType;	// 파일 종류
	
}
