package com.EduTech.entity.event;

import java.time.LocalDateTime;

import com.EduTech.entity.member.Member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "event_reserve", uniqueConstraints = {
@UniqueConstraint(columnNames = {"memId", "event_num"})
}) //한 사용자가 같은 행사에 신청 불가
@AllArgsConstructor
@NoArgsConstructor
@Data
public class EventReserve {

	@Id
	@Column(nullable = false) //행사신청번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long evtRevNum;
	
	@Column(nullable = false) //신청일
	private LocalDateTime fileType;
	
	@Enumerated(EnumType.STRING) //상태
	@Column(nullable = false)
	private EventState state;
		
	@ManyToOne(fetch = FetchType.LAZY) //한 명의 회원이 여러 개의 신청 가능
	@JoinColumn(name = "memId") //회원아이디
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY) //여러명이 하나의 프로그램에 신청 가능
	@JoinColumn(name = "event_num", nullable = false) //행사아이디
	private Event event;
	
}

//모집인원이 다 차면 신청 불가
//같은 행사에 동일 신청 불가
//회원인 경우만 예약 가능
//모집대상자만 신청 가능

//하루 전에는 예약 취소 불가
//예약 취소 여부 재확인


	
