package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
public class EventServiceTestApplyCancel {

    @Autowired
    private EventService eventService;

    @Autowired
    private MemberRepository memberRepository;

    private Member user;

    @BeforeEach
    public void setup() {
        user = Member.builder()
                .memId("applicant4")
                .pw("pw123")
                .name("테스트사용자4")
                .gender(MemberGender.FEMALE)
                .birthDate(LocalDate.of(1990, 6, 15))
                .phone("01011234215")
                .addr("서울시 마포구")
                .email("applicant4@test.com")
                .checkSms(true)
                .checkEmail(false)
                .role(MemberRole.USER)
                .state(MemberState.NORMAL)
                .build();
        memberRepository.save(user);
        System.out.println("테스트 사용자 저장 완료: " + user.getMemId());
    }

    //@Test
    @DisplayName("1. 사용자 프로그램 신청 테스트")
    void testApplyEvent() {
        System.out.println("\n [테스트 1] 사용자 프로그램 신청 테스트 시작");
        try {
            // Given
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

            MockMultipartFile file = new MockMultipartFile("file", "event.pdf", "application/pdf", "<<data>>".getBytes());
            eventService.registerEvent(dto, file);
            System.out.println("이벤트 등록 완료: " + dto.getEventName());

            Long eventNum = eventService.getAllEvents().stream()
                    .filter(e -> e.getEventName().equals("신청 테스트 이벤트"))
                    .findFirst()
                    .orElseThrow()
                    .getEventNum();

            EventApplyRequestDTO applyDTO = EventApplyRequestDTO.builder()
                    .eventNum(eventNum)
                    .memId(user.getMemId())
                    .build();

            eventService.applyEvent(applyDTO);
            System.out.println("신청 완료: eventNum=" + eventNum);

            boolean alreadyApplied = eventService.isAlreadyApplied(eventNum, user.getMemId());
            assertTrue(alreadyApplied);
            System.out.println("신청 여부 확인: " + alreadyApplied);

            List<EventUseDTO> applicants = eventService.getApplicantsByEvent(eventNum);
            assertFalse(applicants.isEmpty());
            System.out.println("신청자 수: " + applicants.size() + ", 첫 번째 신청자 ID: " + applicants.get(0).getMemId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("❌ 예외 발생: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("2. 사용자 프로그램 신청 취소 테스트(취소후 재신청까지)")
    public void testCancelEvent() {
        System.out.println("\n [테스트 2] 사용자 프로그램 신청 취소 테스트 시작");
        try {
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

            MockMultipartFile file = new MockMultipartFile("file", "cancel.pdf", "application/pdf", "<<cancel>>".getBytes());
            eventService.registerEvent(dto, file);
            System.out.println("이벤트 등록 완료: " + dto.getEventName());

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
            System.out.println("신청 완료 → 취소 준비");

            Long evtRevNum = eventService.getApplicantsByEvent(eventNum).get(0).getEvtRevNum();

            eventService.cancelEvent(evtRevNum);
            System.out.println("신청 취소 완료");

            boolean reapplied = eventService.isAlreadyApplied(eventNum, user.getMemId());
            assertFalse(reapplied);
            System.out.println("신청 여부(취소 후): " + reapplied);

        } catch (Exception e) {
            e.printStackTrace();
            fail("예외 발생: " + e.getMessage());
        }
    }

    //@Test
    @DisplayName("3. 신청 여부 확인 테스트")
    public void testIsAlreadyApplied() {
        System.out.println("\n [테스트 3] 신청 여부 확인 테스트 시작");
        try {
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

            MockMultipartFile file = new MockMultipartFile("file", "applycheck.pdf", "application/pdf", "<<applycheck>>".getBytes());
            eventService.registerEvent(dto, file);
            Long eventNum = eventService.getAllEvents().stream()
                    .filter(e -> e.getEventName().equals("신청 여부 확인 이벤트"))
                    .findFirst()
                    .orElseThrow()
                    .getEventNum();

            boolean beforeApply = eventService.isAlreadyApplied(eventNum, user.getMemId());
            System.out.println("신청 전 상태: " + beforeApply);
            assertFalse(beforeApply);

            EventApplyRequestDTO applyDTO = EventApplyRequestDTO.builder()
                    .eventNum(eventNum)
                    .memId(user.getMemId())
                    .build();
            eventService.applyEvent(applyDTO);

            boolean afterApply = eventService.isAlreadyApplied(eventNum, user.getMemId());
            System.out.println("신청 후 상태: " + afterApply);
            assertTrue(afterApply);

        } catch (Exception e) {
            e.printStackTrace();
            fail("예외 발생: " + e.getMessage());
        }
    }

    //@Test
    @DisplayName("4. 신청 불가능한 기간 확인 테스트")
    public void testApplyEventOutsidePeriod() {
        System.out.println("\n [테스트 4] 신청 불가능한 기간 테스트 시작");
        try {
            EventInfoDTO dto = EventInfoDTO.builder()
                    .eventName("기간 외 테스트")
                    .eventInfo("기간 테스트 설명")
                    .place("7층 강의실")
                    .category(EventCategory.USER)
                    .applyStartPeriod(LocalDateTime.now().plusDays(2)) // 미래 시작
                    .applyEndPeriod(LocalDateTime.now().plusDays(5))
                    .eventStartPeriod(LocalDateTime.now().plusDays(6))
                    .eventEndPeriod(LocalDateTime.now().plusDays(10))
                    .maxCapacity(15)
                    .daysOfWeek(List.of(4)) // THURSDAY = 4
                    .build();

            MockMultipartFile file = new MockMultipartFile(
                    "file", "invalid.pdf", "application/pdf", "<<invalid>>".getBytes());

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

            try {
                eventService.applyEvent(applyDTO);
                fail("예외가 발생하지 않았습니다.");
            } catch (IllegalStateException e) {
                System.out.println("신청 실패 예외 메시지: " + e.getMessage());
                assertEquals("신청 기간이 아닙니다.", e.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail("예외 발생: " + e.getMessage());
        }
    }

}