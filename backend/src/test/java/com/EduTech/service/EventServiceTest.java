package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.*;

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
import com.EduTech.dto.event.EventUseDTO;
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
public class EventServiceTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private MemberRepository memberRepository;

    private Member user;

    @BeforeEach
    public void setup() {
        user = Member.builder()
                .memId("applicant1")
                .pw("pw123")
                .name("테스트사용자")
                .gender(MemberGender.FEMALE)
                .birthDate(LocalDate.of(1990, 6, 15))
                .phone("01012312312")
                .addr("서울시 마포구")
                .email("applicant1@test.com")
                .checkSms(true)
                .checkEmail(false)
                .role(MemberRole.USER)
                .state(MemberState.NORMAL)
                .build();
        memberRepository.save(user);
    }

    //@Test
    @DisplayName("1. 사용자 프로그램 신청 테스트")
    public void testApplyEvent() {
        // given
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("신청 테스트 이벤트")
                .eventInfo("신청 테스트 설명")
                .place("5층 강의실")
                .category(EventCategory.USER)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(2))
                .eventStartPeriod(LocalDateTime.now().plusDays(5))
                .eventEndPeriod(LocalDateTime.now().plusDays(10))
                .maxCapacity(20)
                .daysOfWeek(List.of(DayOfWeek.MONDAY.getValue(), DayOfWeek.WEDNESDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "event.pdf", "application/pdf", "<<data>>".getBytes()
        );

        eventService.registerEvent(dto, file);

        Long eventNum = eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals("신청 테스트 이벤트"))
                .findFirst()
                .orElseThrow()
                .getEventNum();

        EventApplyRequestDTO applyDTO = EventApplyRequestDTO.builder()
                .eventNum(eventNum)
                .memId(user.getMemId())
                .build();

        // when
        eventService.applyEvent(applyDTO);

        // then
        boolean alreadyApplied = eventService.isAlreadyApplied(eventNum, user.getMemId());
        assertTrue(alreadyApplied);

        List<EventUseDTO> applicants = eventService.getApplicantsByEvent(eventNum);
        assertFalse(applicants.isEmpty());
        assertEquals(user.getMemId(), applicants.get(0).getMemId());
    }

    //@Test
    @DisplayName("2. 사용자 프로그램 신청 취소 테스트")
    public void testCancelEvent() {
        // given
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("취소 테스트 이벤트")
                .eventInfo("신청 취소 테스트 설명")
                .place("6층 강의실")
                .category(EventCategory.USER)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(2))
                .eventStartPeriod(LocalDateTime.now().plusDays(3))
                .eventEndPeriod(LocalDateTime.now().plusDays(7))
                .maxCapacity(25)
                .daysOfWeek(List.of(DayOfWeek.TUESDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "cancel.pdf", "application/pdf", "<<cancel>>".getBytes()
        );

        eventService.registerEvent(dto, file);

        Long eventNum = eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals("취소 테스트 이벤트"))
                .findFirst()
                .orElseThrow()
                .getEventNum();

        EventApplyRequestDTO applyDTO = EventApplyRequestDTO.builder()
                .eventNum(eventNum)
                .memId(user.getMemId())
                .build();

        eventService.applyEvent(applyDTO);
        List<EventUseDTO> list = eventService.getApplicantsByEvent(eventNum);
        Long evtRevNum = list.get(0).getEvtRevNum();

        // when
        eventService.cancelEvent(evtRevNum);

        // then
        boolean reapplied = eventService.isAlreadyApplied(eventNum, user.getMemId());
        assertFalse(reapplied);
    }
    
    @Test
    @DisplayName("3. 신청 여부 확인 테스트")
    public void testIsAlreadyApplied() {
        // given
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("신청 여부 확인 이벤트")
                .eventInfo("신청 여부 테스트 설명")
                .place("8층 강의실")
                .category(EventCategory.USER)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(2))
                .eventStartPeriod(LocalDateTime.now().plusDays(3))
                .eventEndPeriod(LocalDateTime.now().plusDays(7))
                .maxCapacity(25)
                .daysOfWeek(List.of(DayOfWeek.TUESDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "applycheck.pdf", "application/pdf", "<<applycheck>>".getBytes()
        );

        eventService.registerEvent(dto, file);

        Long eventNum = eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals("신청 여부 확인 이벤트"))
                .findFirst()
                .orElseThrow()
                .getEventNum();

        // when - 신청 전
        boolean beforeApply = eventService.isAlreadyApplied(eventNum, user.getMemId());
        assertFalse(beforeApply, "신청 전이므로 false여야 합니다.");

        // when - 신청 후
        EventApplyRequestDTO applyDTO = EventApplyRequestDTO.builder()
                .eventNum(eventNum)
                .memId(user.getMemId())
                .build();

        eventService.applyEvent(applyDTO);

        boolean afterApply = eventService.isAlreadyApplied(eventNum, user.getMemId());
        assertTrue(afterApply, "신청 후이므로 true여야 합니다.");
    }

    //@Test
    @DisplayName("4. 신청 불가능한 기간 확인 테스트")
    public void testApplyEventOutsidePeriod() {
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("기간 외 테스트")
                .eventInfo("기간 테스트 설명")
                .place("7층 강의실")
                .category(EventCategory.USER)
                .applyStartPeriod(LocalDateTime.now().plusDays(2)) // 시작 전
                .applyEndPeriod(LocalDateTime.now().plusDays(5))
                .eventStartPeriod(LocalDateTime.now().plusDays(6))
                .eventEndPeriod(LocalDateTime.now().plusDays(10))
                .maxCapacity(15)
                .daysOfWeek(List.of(DayOfWeek.THURSDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "invalid.pdf", "application/pdf", "<<invalid>>".getBytes()
        );

        eventService.registerEvent(dto, file);

        Long eventNum = eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals("기간 외 테스트"))
                .findFirst()
                .orElseThrow()
                .getEventNum();

        EventApplyRequestDTO applyDTO = EventApplyRequestDTO.builder()
                .eventNum(eventNum)
                .memId(user.getMemId())
                .build();

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            eventService.applyEvent(applyDTO);
        });

        assertEquals("신청 기간이 아닙니다.", exception.getMessage());
    }
}
