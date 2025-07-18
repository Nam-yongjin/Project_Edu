package com.EduTech.entity.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale.Category;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "se_event")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Event {

	@Id
	@Column(nullable = false) //행사 아이디
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long eventNum;
	
	@Column(nullable = false) //행사명
	private String eventName;
	
	@Column(nullable = false) //장소
	private String place;
	
	@Column(nullable = false) //소개
	private String eventInfo;
	
	@Enumerated(EnumType.STRING) //분류
	@Column(nullable = false)
	private Category category;
	
	@Column(nullable = false) //신청시작기간
	private LocalDateTime applyStartPeriod;
	
	@Column(nullable = false) //신청종료기간
	private LocalDateTime applyEndPeriod;
	
	@Column(nullable = false) //진행시작기간
	private LocalDateTime progressStartPeriod;
	
	@Column(nullable = false) //진행종료기간
	private LocalDateTime progressEndPeriod;
	
	@Column(nullable = false) //모집대상
	private String target;
	
	@Column(nullable = false) //현재인원
	private int currCapacity;
	
	@Column(nullable = false) //모집인원
	private int totCapacity;
	
	@Column //기타유의사항
	private String etc;
	
	@Enumerated(EnumType.STRING) //상태
	@Column(nullable = false)
	private EventState state;
	
	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true) //하나의 행사 여러 명이 신청 가능, 행사 삭제되면 신청도 같이 삭제
	private List<EventReserve> eventReserves = new ArrayList<>(); //null방지 초기화	

	@OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true) //하나의 행사 여러 개의 파일 첨부 가능, 행사 삭제되면 파일도 같이 삭제
	private List<EventFile> eventFiles = new ArrayList<>();
	
}

