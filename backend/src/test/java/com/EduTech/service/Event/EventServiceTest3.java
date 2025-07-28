package com.EduTech.service.Event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
public class EventServiceTest3 {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private MemberRepository memberRepository;
    
    private Member member;

    @BeforeEach
    public void setUpMember() {
        member = Member.builder()
                .memId("testUser04")
                .pw("pw1234")
                .name("테스트 사용자3")
                .gender(MemberGender.FEMALE)
                .birthDate(LocalDate.of(2008, 5, 10))
                .phone("01012122323")
                .addr("서울시 중구")
                .email("user05@test.com")
                .checkSms(true)
                .checkEmail(true)
                .role(MemberRole.USER)
                .state(MemberState.NORMAL)
                .build();

        memberRepository.save(member);
    }

    private Long registerTestEvent(String eventName) {
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName(eventName)
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

        return eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals(eventName))
                .findFirst().orElseThrow()
                .getEventNum();
    }

    private EventApplyRequestDTO createApplyDto(Long eventNum) {
        return EventApplyRequestDTO.builder()
                .eventNum(eventNum)
                .memId(member.getMemId())
                .build();
    }

    //@Test
    @DisplayName(" 정상적으로 이벤트 신청이 처리된다")
    public void 신청_정상처리됨() {
        // given
        Long eventNum = registerTestEvent("정상 신청 이벤트");
        EventApplyRequestDTO dto = createApplyDto(eventNum);

        // when & then
        assertDoesNotThrow(() -> eventService.applyEvent(dto),
                "정상적인 이벤트 신청에 예외가 발생하면 안 됨");
    }

    @Test
    @DisplayName(" 중복 신청 시 IllegalStateException이 발생한다")
    public void testApplyEvent_Duplicate() {
        // given
        Long eventNum = registerTestEvent("중복 테스트");
        EventApplyRequestDTO dto = EventApplyRequestDTO.builder()
                .eventNum(eventNum)
                .memId(member.getMemId())
                .build();

        eventService.applyEvent(dto); // 첫 번째 신청 성공

        // when & then
        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> {
            eventService.applyEvent(dto); // 두 번째 신청은 예외
        });

        assertEquals("이미 신청한 프로그램입니다.", ex.getMessage());
    }
}
