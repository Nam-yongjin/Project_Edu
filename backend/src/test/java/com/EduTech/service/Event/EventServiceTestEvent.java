package com.EduTech.service.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
public class EventServiceTestEvent {

    @Autowired
    private EventService eventService;

    @Autowired
    private MemberRepository memberRepository;

    private Member user;

    @BeforeEach
    public void setup() {
    	user = Member.builder()
                .memId("user44")
                .pw("12345")
                .name("일반")
                .gender(MemberGender.MALE)
                .birthDate(LocalDate.of(1985, 1, 1))
                .phone("01023423454")
                .addr("서울시 강남구")
                .email("user44@edutech.com")
                .checkSms(true)
                .checkEmail(true)
                .role(MemberRole.USER)
                .state(MemberState.NORMAL)
                .build();

        memberRepository.save(user);
    }

    @Test
    @DisplayName("1. 프로그램 등록 테스트")
    public void registerEventTest() {
        try {
            EventInfoDTO dto = EventInfoDTO.builder()
                    .eventName("서비스 테스트 이벤트")
                    .eventInfo("서비스 테스트용 설명입니다.")
                    .place("2층 강의실")
                    .category(EventCategory.USER) 
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

    @Test
    @DisplayName("2. 프로그램 상세 조회")
    public void getEventTest() {
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("조회 테스트")
                .eventInfo("조회 테스트 설명")
                .place("3층 강의실")
                .category(EventCategory.USER) 
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

    @Test
    @DisplayName("3. 신청 가능 여부 확인")
    public void isAvailableTest() {
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("신청 가능 테스트")
                .eventInfo("가능 여부 확인")
                .place("지하 강당")
                .category(EventCategory.USER) 
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
    
    @Test
    @DisplayName("4. 프로그램 수정 테스트")
    public void updateEventTest() {
        // given
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("수정 테스트")
                .eventInfo("수정 전 내용")
                .place("1층 강의실")
                .category(EventCategory.USER)
                .applyStartPeriod(LocalDateTime.now().minusDays(2))
                .applyEndPeriod(LocalDateTime.now().plusDays(2))
                .eventStartPeriod(LocalDateTime.now().plusDays(5))
                .eventEndPeriod(LocalDateTime.now().plusDays(10))
                .maxCapacity(20)
                .daysOfWeek(List.of(DayOfWeek.THURSDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "original.pdf", "application/pdf", "<<original>>".getBytes()
        );

        eventService.registerEvent(dto, file);

        // 이벤트 번호 조회
        Long eventNum = eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals("수정 테스트"))
                .findFirst()
                .orElseThrow()
                .getEventNum();

        // when - 수정 실행
        EventInfoDTO updateDto = EventInfoDTO.builder()
                .eventName("수정 완료")
                .eventInfo("수정된 설명입니다.")
                .place("수정된 장소")
                .category(EventCategory.USER)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(3))
                .eventStartPeriod(LocalDateTime.now().plusDays(6))
                .eventEndPeriod(LocalDateTime.now().plusDays(11))
                .maxCapacity(50)
                .daysOfWeek(List.of(DayOfWeek.FRIDAY.getValue()))
                .build();

        MockMultipartFile newFile = new MockMultipartFile(
                "file", "updated.pdf", "application/pdf", "<<updated>>".getBytes()
        );

        eventService.updateEvent(eventNum, updateDto, newFile);

        // then
        EventInfoDTO result = eventService.getEvent(eventNum);
        assertEquals("수정 완료", result.getEventName());
        assertEquals("수정된 장소", result.getPlace());
        assertEquals(50, result.getMaxCapacity());
    }
    
    @Test
    @DisplayName("5. 프로그램 삭제 테스트")
    public void deleteEventTest() {
        // given
        EventInfoDTO dto = EventInfoDTO.builder()
                .eventName("삭제 테스트")
                .eventInfo("삭제 대상 설명입니다.")
                .place("4층 강의실")
                .category(EventCategory.USER)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(4))
                .eventStartPeriod(LocalDateTime.now().plusDays(6))
                .eventEndPeriod(LocalDateTime.now().plusDays(12))
                .maxCapacity(40)
                .daysOfWeek(List.of(DayOfWeek.SATURDAY.getValue()))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "delete.pdf", "application/pdf", "<<delete>>".getBytes()
        );

        eventService.registerEvent(dto, file);
        Long eventNum = eventService.getAllEvents().stream()
                .filter(e -> e.getEventName().equals("삭제 테스트"))
                .findFirst()
                .orElseThrow()
                .getEventNum();

        // when
        eventService.deleteEvent(eventNum);

        // then - 삭제된 이벤트는 조회하면 예외 발생
        boolean exists = eventService.getAllEvents().stream()
                .anyMatch(e -> e.getEventNum().equals(eventNum));
        assertTrue(!exists); // 삭제되었으므로 false여야 함
    }
    

    // 추가로 다음 테스트도 추천합니다:
    // - applyEventTest: dto 기반 신청 시 예외 없이 처리되는지
    // - cancelEventTest: 신청 내역 삭제 시 정상 동작 확인
    // - registerBannerTest: 이미지 등록, 파일 확장자 체크 포함
}
