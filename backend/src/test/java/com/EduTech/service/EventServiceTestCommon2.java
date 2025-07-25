package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.*;
import com.EduTech.entity.member.*;
import com.EduTech.repository.event.*;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.event.EventServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class EventServiceTestCommon2 {

    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventInfoRepository infoRepository;

    @Autowired
    private EventUseRepository useRepository;

    private Member member;
    
    // 랜덤 값 생성
    String randomMemId = generateRandomString("user_", 5);
    String randomPhone = generateRandomPhone();
    String randomEmail = generateRandomEmail();
    
    private final String fixedEmail = "hong@test.com";

    @BeforeEach
    void setUp() {
        member = Member.builder()
        		.memId(randomMemId)
                .pw("1234")
                .name("홍길동")
                .gender(MemberGender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .phone(randomPhone)
                .addr("서울시 테스트구")
                .email(fixedEmail)
                .checkSms(true)
                .checkEmail(true)
                .role(MemberRole.USER)
                .state(MemberState.NORMAL)
                .build();
        memberRepository.save(member);
    }

    @Test
    @DisplayName("✅ EventUse → DTO 변환 테스트 (직접 호출)")
    @Rollback
    public void testToDTO() throws Exception {
        // GIVEN
        EventInfo event = infoRepository.save(EventInfo.builder()
                .eventName("DTO 변환 테스트")
                .eventInfo("DTO용 행사")
                .place("1층")
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(2))
                .eventStartPeriod(LocalDateTime.now().plusDays(3))
                .eventEndPeriod(LocalDateTime.now().plusDays(5))
                .applyAt(LocalDateTime.now())
                .category(EventCategory.USER)
                .maxCapacity(30)
                .originalName("event.pdf")
                .filePath("/dummy/path")
                .daysOfWeek(List.of(1, 3))
                .build());

        EventUse use = useRepository.save(EventUse.builder()
                .eventInfo(event)
                .member(member)
                .revState(RevState.APPROVED)
                .applyAt(LocalDateTime.now())
                .build());

        // WHEN
        EventUseDTO dto = eventService.toDTO(use); // 직접 호출

        // THEN
        System.out.println("===== EventUse → DTO 테스트 결과 =====");
        System.out.println("행사명: " + dto.getEventName());
        System.out.println("신청자명: " + dto.getName());
        System.out.println("이메일: " + dto.getEmail());
        System.out.println("신청 상태: " + dto.getRevState());
        System.out.println("======================================");

        assertEquals("DTO 변환 테스트", dto.getEventName());
        assertEquals("홍길동", dto.getName());
        assertEquals("hong@test.com", dto.getEmail());
        assertEquals("승인", dto.getRevState()); // RevState enum의 toString() 또는 별도 매핑 필요
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
