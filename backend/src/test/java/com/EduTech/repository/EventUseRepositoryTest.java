package com.EduTech.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.event.EventUse;
import com.EduTech.entity.event.RevState;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventUseRepository;
import com.EduTech.repository.member.MemberRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class EventUseRepositoryTest {

	@Autowired
	EventUseRepository eventUseRepository;
	
	@Autowired
	EventInfoRepository eventInfoRepository;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Test
	@Transactional
	@Rollback(false)
	@DisplayName("프로그램 이용 신청")
	public void applyProgramUseTest() {

	    Member member = Member.builder()
	        .memId("0123456789123456")
	        .pw("1234")
	        .name("테스터")
	        .birthDate(LocalDate.of(2000, 1, 1))
	        .phone("01012345678")
	        .addr("대구광역시")
	        .email("tester@example.com")
	        .checkSms(true)
	        .checkEmail(true)
	        .role(MemberRole.USER)
	        .state(MemberState.NORMAL)
	        .build();
	    memberRepository.save(member);
	        
	        EventInfo eventInfo = EventInfo.builder()
	                .eventName("테스트 프로그램")
	                .applyStartPeriod(LocalDateTime.of(2025, 5, 1, 10, 0))
	                .applyEndPeriod(LocalDateTime.of(2025, 5, 31, 18, 0))
	                .daysOfWeek(List.of(DayOfWeek.MONDAY.getValue(), DayOfWeek.WEDNESDAY.getValue()))
	                .place("1층 강의실")
	                .eventInfo("테스트")
	                .eventStartPeriod(LocalDateTime.of(2025, 6, 1, 14, 0))
	                .eventEndPeriod(LocalDateTime.of(2025, 6, 30, 16, 0))
	                .category(EventCategory.USER) 
	                .maxCapacity(20)
	                .originalName("test.pdf")
	                .filePath("/files/test.pdf")
	                .state(EventState.BEFORE)
	                .currCapacity(0)
	                .build();
	        eventInfoRepository.save(eventInfo);

	        EventUse eventUse = EventUse.builder()
	                .applyAt(LocalDateTime.of(2025, 5, 12, 10, 35))
	                .eventInfo(eventInfo)
	                .member(member)
	                .revState(RevState.WAITING)  // ✅ 여기에 명시해야 함
	                .build();

	        EventUse saved = eventUseRepository.save(eventUse);
	        System.out.println("프로그램 신청 시간: " + saved.getApplyAt());
	        
	        // ✅ 결과 검증
	        assert saved.getEvtRevNum() != null;
	        assert saved.getMember().getName().equals("테스터");
		
		
	}
	
	@Test
	@Transactional
	@Rollback(false)
	@DisplayName("프로그램 이용내역 조회")
	public void findProgramUseTest() {
		List<EventUse> list = eventUseRepository.findAll();

	    for (EventUse use : list) {
	        System.out.println("신청번호: " + use.getEvtRevNum());
	        System.out.println("신청일시: " + use.getApplyAt());
	        System.out.println("프로그램명: " + use.getEventInfo().getEventName());
	        System.out.println("신청자명: " + use.getMember().getName());
	        System.out.println("--------");
        
        
	    }
	}
	
	@Test
	@Transactional
	@Rollback(false)
	@DisplayName("프로그램 신청내역 삭제")
	public void deleteEventUseTest() {
	    Long evtRevNum = 1L; // 또는 저장된 실제 신청번호 사용

	    boolean exists = eventUseRepository.existsById(evtRevNum);
	    if (exists) {
	        eventUseRepository.deleteById(evtRevNum);
	        System.out.println("신청번호 " + evtRevNum + " 삭제 완료");
	    } else {
	        System.out.println("신청번호 " + evtRevNum + " 는 존재하지 않습니다.");
	    }
	}
	
	
}