package com.EduTech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;

import com.EduTech.dto.event.EventInfoDTO;
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

    @Autowired
    private ModelMapper modelMapper;

    private Member admin;

    @BeforeEach
    public void setup() {
        admin = Member.builder()
                .memId("admin01")
                .pw("1234")
                .name("관리자")
                .gender(MemberGender.MALE)
                .birthDate(LocalDate.of(1985, 1, 1))
                .phone("01012345678")
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
    @DisplayName("1. 프로그램 등록 테스트")
    public void registerEventTest() {
        try {
            EventInfoDTO dto = EventInfoDTO.builder()
                    .eventName("서비스 테스트 이벤트")
                    .eventInfo("서비스 테스트용 설명입니다.")
                    .place("2층 강의실")
                    .target("청소년")
                    .applyStartPeriod(LocalDateTime.now().minusDays(1))
                    .applyEndPeriod(LocalDateTime.now().plusDays(5))
                    .eventStartPeriod(LocalDateTime.now().plusDays(10))
                    .eventEndPeriod(LocalDateTime.now().plusDays(15))
                    .maxCapacity(30)
                    .daysOfWeek(List.of(DayOfWeek.MONDAY.getValue(), DayOfWeek.WEDNESDAY.getValue()))
                    .build();

            MockMultipartFile file = new MockMultipartFile(
                    "file", "test.pdf", "application/pdf", "<<pdf content>>".getBytes()
            );

            // when
            eventService.registerEvent(dto, file);

            // then
            List<EventInfoDTO> allEvents = eventService.getAllEvents();
            assertTrue(allEvents.stream().anyMatch(e -> e.getEventName().equals("서비스 테스트 이벤트")));

        } catch (Exception e) {
            e.printStackTrace(); // 👈 예외 메시지를 여기서 꼭 확인
            throw e;
        }
    }

    //@Test
    @DisplayName("2. 프로그램 상세 조회")
    public void getEventTest() {
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("조회 테스트")
                .eventInfo("조회 테스트 설명")
                .place("3층 강의실")
                .target("성인")
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(5))
                .eventStartPeriod(LocalDateTime.now().plusDays(10))
                .eventEndPeriod(LocalDateTime.now().plusDays(15))
                .maxCapacity(50)
                .daysOfWeek(List.of(DayOfWeek.TUESDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "manual.pdf", "application/pdf", "<<data>>".getBytes()
        );

        eventService.registerEvent(dto, file);
        List<EventInfoDTO> events = eventService.getAllEvents();
        Long eventNum = events.stream().filter(e -> e.getEventName().equals("조회 테스트"))
                .findFirst().orElseThrow().getEventNum();

        EventInfoDTO result = eventService.getEvent(eventNum);
        assertEquals("조회 테스트", result.getEventName());
    }

    //@Test
    @DisplayName("3. 신청 가능 여부 확인")
    public void isAvailableTest() {
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("신청 가능 테스트")
                .eventInfo("가능 여부 확인")
                .place("지하 강당")
                .target("전체")
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(3))
                .eventStartPeriod(LocalDateTime.now().plusDays(5))
                .eventEndPeriod(LocalDateTime.now().plusDays(10))
                .maxCapacity(10)
                .daysOfWeek(List.of(DayOfWeek.FRIDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "guide.pdf", "application/pdf", "<<data>>".getBytes()
        );

        eventService.registerEvent(dto, file);
        Long eventNum = eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals("신청 가능 테스트"))
                .findFirst().orElseThrow().getEventNum();

        assertTrue(eventService.isAvailable(eventNum));
    }

    // 추가로 다음 테스트도 추천합니다:
    // - applyEventTest: dto 기반 신청 시 예외 없이 처리되는지
    // - cancelEventTest: 신청 내역 삭제 시 정상 동작 확인
    // - registerBannerTest: 이미지 등록, 파일 확장자 체크 포함
}
