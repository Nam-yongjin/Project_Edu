package com.EduTech.entity.event;

import java.time.LocalDateTime;

import com.EduTech.entity.member.Member;

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
public class EventUse {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long evtRevNum; // 프로그램번호

	@Column(nullable = false)
	private LocalDateTime applyAt; // 프로그램 신청일
	
	@Enumerated(EnumType.STRING) //상태
	@Column(name = "rev_state", nullable = false)
	private RevState revState;

	// FK 프로그램 정보 id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eventNum", nullable = false)
	private EventInfo eventInfo;

	// FK 회원id
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "memId", nullable = false)
	private Member member;
}