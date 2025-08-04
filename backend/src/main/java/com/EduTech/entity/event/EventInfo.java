package com.EduTech.entity.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
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
	private String eventName; // 행사 이름

	@Column(nullable = false, columnDefinition = "TEXT")
	private String eventInfo; // 행사 상세 내용
	
	@Builder.Default
	@Column(nullable = false)
	private LocalDateTime applyAt = LocalDateTime.now(); // 동록일
	
	@Column(nullable = false)
	private LocalDateTime applyStartPeriod; // 신청시작기간

	@Column(nullable = false)
	private LocalDateTime applyEndPeriod; // 신청종료기간
	
	@Enumerated(EnumType.STRING)
	private EventCategory category;	// 유저, 학생, 선생

	@Enumerated(EnumType.STRING)
	@Column(nullable = false) // 신청전 / 신청중 / 신청마감 등
	private EventState state;

	@Builder.Default
	@ElementCollection
	@CollectionTable(name = "event_days", joinColumns = @JoinColumn(name = "eventNum"))
	@Column(nullable = false)
	private List<Integer> daysOfWeek  = new ArrayList<>();
	
	@Column(nullable = false, length = 20)
	private String place; // 장소

	@Column(nullable = false)
	private LocalDateTime eventStartPeriod; // 행사시작기간

	@Column(nullable = false)
	private LocalDateTime eventEndPeriod; // 행사종료기간

	@Column(nullable = false)
	private int maxCapacity; // 모집인원
	
	@Column(nullable = false)
	private int currCapacity; // 현재인원
	
	@Column(length = 1000)
	private String etc; // 기타 유의사항

	@Column(length = 100)
	private String originalName; // 파일명

	@Column(length = 200)
	private String filePath; 	// 파일경로
	
	@Column(length = 200)
	private String mainImagePath;

	@OneToMany(mappedBy = "eventInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventImage> imageList = new ArrayList<>();

	@OneToMany(mappedBy = "eventInfo", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<EventFile> attachList = new ArrayList<>();
	
	@PrePersist
	public void prePersist() {
	    if (this.state == null) {
	        this.state = EventState.BEFORE; // 또는 기본 상태
	    }
	}
	
}
