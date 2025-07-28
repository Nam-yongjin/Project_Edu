package com.EduTech.service.Event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.RevState;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.event.EventService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
public class EventServiceTest2 {

    @Autowired
    private EventService eventService;

    @Autowired
    private MemberRepository memberRepository;

    private Member admin;

    @BeforeEach
    public void setup() {
        admin = Member.builder()
                .memId("admin01")
                .pw("1234")
                .name("관리자")
                .gender(MemberGender.MALE)
                .birthDate(LocalDate.of(1985, 1, 1))
                .phone("12345678901")
                .addr("서울시 강남구")
                .email("admin@edutech.com")
                .checkSms(true)
                .checkEmail(true)
                .role(MemberRole.ADMIN)
                .state(MemberState.NORMAL)
                .build();

        memberRepository.save(admin);
    }

    @Test
    @DisplayName("4. DTO 기반 신청 테스트 (예외 없이 처리)")
    public void applyEventTest() {
        // 1. 사용자 등록
        Member member = Member.builder()
                .memId("testUser02")
                .pw("pw12345")
                .name("테스트 사용자")
                .gender(MemberGender.FEMALE)
                .birthDate(LocalDate.of(2008, 5, 10)) // 청소년
                .phone("01021211212")
                .addr("서울시 중구")
                .email("user02@test.com")
                .checkSms(true)
                .checkEmail(true)
                .role(MemberRole.USER)
                .state(MemberState.NORMAL)
                .build();
        memberRepository.save(member);

        // 2. 프로그램 등록
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("신청 가능 이벤트")
                .eventInfo("테스트용 신청 프로그램")
                .place("4층 강의실")
                .category(EventCategory.USER) 
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(5))
                .eventStartPeriod(LocalDateTime.now().plusDays(10))
                .eventEndPeriod(LocalDateTime.now().plusDays(15))
                .maxCapacity(20)
                .daysOfWeek(List.of(DayOfWeek.WEDNESDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "intro.pdf", "application/pdf", "<<pdf content>>".getBytes()
        );

        eventService.registerEvent(dto, file);

        // 3. 저장된 이벤트 번호 가져오기
        Long eventNum = eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals("신청 가능 이벤트"))
                .findFirst().orElseThrow().getEventNum();

        // 4. 신청 DTO 생성
        EventApplyRequestDTO applyDTO = EventApplyRequestDTO.builder()
                .eventNum(eventNum)
                .memId(member.getMemId())
                .revState(RevState.WAITING)
                .build();

        // 5. 예외 없이 처리되는지 검증
        assertDoesNotThrow(() -> {
            eventService.applyEvent(applyDTO);
        });
        
    }


    // 추가로 다음 테스트도 추천합니다:
    // - applyEventTest: dto 기반 신청 시 예외 없이 처리되는지
    // - cancelEventTest: 신청 내역 삭제 시 정상 동작 확인
    // - registerBannerTest: 이미지 등록, 파일 확장자 체크 포함
}
