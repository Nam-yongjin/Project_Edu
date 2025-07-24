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
	@DisplayName("✅ 프로그램 이용 신청 테스트 - 랜덤 데이터")
	public void applyProgramUseTest() {

	    // 랜덤 값 생성
	    String randomMemId = generateRandomString("user_", 5);
	    String randomPhone = generateRandomPhone();
	    String randomEmail = generateRandomEmail();

	    // 🔷 1. 회원 저장
	    Member member = Member.builder()
	        .memId(randomMemId)
	        .pw("1234")
	        .name("양희찬")
	        .birthDate(LocalDate.of(2000, 7, 15))
	        .phone(randomPhone)
	        .addr("대구광역시")
	        .email(randomEmail)
	        .checkSms(true)
	        .checkEmail(true)
	        .role(MemberRole.ADMIN)
	        .state(MemberState.NORMAL)
	        .build();
	    memberRepository.save(member);

	    System.out.println("\n🔹 [회원 등록 완료]");
	    System.out.println("  ▶ ID: " + member.getMemId());
	    System.out.println("  ▶ 이메일: " + member.getEmail());
	    System.out.println("  ▶ 전화번호: " + member.getPhone());

	    // 🔷 2. 행사 저장
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

	    System.out.println("\n🔹 [행사 등록 완료]");
	    System.out.println("  ▶ 행사명: " + eventInfo.getEventName());

	    // 🔷 3. 신청 저장
	    EventUse eventUse = EventUse.builder()
	            .applyAt(LocalDateTime.of(2025, 5, 12, 10, 35))
	            .eventInfo(eventInfo)
	            .member(member)
	            .revState(RevState.WAITING)
	            .build();

	    EventUse saved = eventUseRepository.save(eventUse);

	    System.out.println("\n✅ [프로그램 신청 완료]");
	    System.out.println("  ▶ 신청자 ID: " + saved.getMember().getMemId());
	    System.out.println("  ▶ 신청자 전화번호: " + saved.getMember().getPhone());
	    System.out.println("  ▶ 신청자 이메일: " + saved.getMember().getEmail());
	    System.out.println("  ▶ 신청 일시: " + saved.getApplyAt());

	    assert saved.getEvtRevNum() != null;
	    assert saved.getMember().getEmail().equals(randomEmail);
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
	@DisplayName("프로그램 신청내역 삭제 테스트")
	public void deleteEventUseTest() {
	    // 가장 최근 신청 내역 조회 (있다면)
	    List<EventUse> uses = eventUseRepository.findAll();
	    if (!uses.isEmpty()) {
	        EventUse last = uses.get(uses.size() - 1);
	        Long evtRevNum = last.getEvtRevNum();

	        eventUseRepository.deleteById(evtRevNum);
	        System.out.println("신청번호 " + evtRevNum + " 삭제 완료");
	    } else {
	        System.out.println("삭제할 신청 내역이 존재하지 않습니다.");
	    }
	}
	
	// 랜덤 문자열 생성 (숫자/영문자)
	private String generateRandomString(String prefix, int length) {
	    String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    StringBuilder sb = new StringBuilder(prefix);
	    for (int i = 0; i < length; i++) {
	        sb.append(chars.charAt((int) (Math.random() * chars.length())));
	    }
	    return sb.toString();
	}

	// 랜덤 휴대폰 번호 생성
	private String generateRandomPhone() {
	    int middle = (int) (Math.random() * 9000) + 1000;
	    int last = (int) (Math.random() * 9000) + 1000;
	    return "010" + middle + last;
	}

	// 랜덤 이메일 생성
	private String generateRandomEmail() {
	    return generateRandomString("user", 6) + "@example.com";
	}
	
	
}