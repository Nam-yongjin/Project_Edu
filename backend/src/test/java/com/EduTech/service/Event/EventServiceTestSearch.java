package com.EduTech.service.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.event.EventService;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
public class EventServiceTestSearch {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private EventInfoRepository eventInfoRepository;
    
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

    // 1. 전체 행사 조회 테스트
  //@Test
  @DisplayName("1. 전체 행사 조회 테스트")
  void testGetAllEvents() {
      System.out.println("[1] 전체 행사 조회 테스트 시작");
      List<EventInfoDTO> events = eventService.getAllEvents();
      System.out.println("조회된 전체 행사 수: " + events.size());
      events.forEach(e ->
          System.out.println(" - 행사명: " + e.getEventName() +
                             ", 신청 종료일: " + e.getApplyEndPeriod())
      );
      assertNotNull(events);
  }

  // 2. 행사 상세 조회 테스트
  //@Test
  @DisplayName("2. 행사 상세 조회 테스트")
  void testGetEventById() {
      System.out.println("[2] 행사 상세 조회 테스트 시작");
      EventInfoDTO dto = EventInfoDTO.builder()
              .eventName("상세조회 테스트")
              .eventInfo("설명입니다.")
              .place("3층")
              .category(EventCategory.USER)
              .applyStartPeriod(LocalDateTime.now().minusDays(1))
              .applyEndPeriod(LocalDateTime.now().plusDays(2))
              .eventStartPeriod(LocalDateTime.now().plusDays(3))
              .eventEndPeriod(LocalDateTime.now().plusDays(5))
              .maxCapacity(30)
              .daysOfWeek(List.of(1, 3))
              .build();

      MockMultipartFile file = new MockMultipartFile("file", "guide.pdf", "application/pdf", "<<data>>".getBytes());
      eventService.registerEvent(dto, file);

      Long eventNum = eventService.getAllEvents().stream()
              .filter(e -> e.getEventName().equals("상세조회 테스트"))
              .findFirst().orElseThrow().getEventNum();

      EventInfoDTO event = eventService.getEvent(eventNum);
      System.out.println("상세 조회 결과:");
      System.out.println(" - 행사명: " + event.getEventName());
      System.out.println(" - 장소: " + event.getPlace());
      System.out.println(" - 설명: " + event.getEventInfo());
      assertEquals("상세조회 테스트", event.getEventName());
  }

  // 3. 행사 목록 페이징 조회
  //@Test
  @DisplayName("3. 행사 목록 페이징 조회")
  void testGetEventList() {
      System.out.println("[3] 행사 목록 페이징 조회 테스트 시작");
      Pageable pageable = PageRequest.of(0, 10);
      Page<EventInfoDTO> result = eventService.getEventList(pageable, "", "", null);
      System.out.println("페이지당 항목 수: " + result.getSize());
      System.out.println("총 항목 수: " + result.getTotalElements());
      assertNotNull(result);
      assertTrue(result.getContent().size() <= 10);
  }

  // 4. 사용자 검색 기반 목록 조회
  //@Test
  @DisplayName("4. 사용자 검색 기반 목록 조회")
  void testSearchEventList() {
      System.out.println("[4] 사용자 검색 기반 목록 조회 시작");
      Pageable pageable = PageRequest.of(0, 5);
      Page<EventInfoDTO> result = eventService.searchEventList(pageable, "eventName", "테스트", null);
      System.out.println("검색된 항목 수: " + result.getTotalElements());
      result.getContent().forEach(e -> System.out.println(" - " + e.getEventName()));
      assertNotNull(result);
  }

  // 5. 특정 사용자의 신청 목록 조회
  //@Test
  @DisplayName("5. 특정 사용자의 신청 목록 조회")
  void testGetUserEventList() {
      System.out.println("[5] 특정 사용자의 신청 목록 조회 시작");

      Pageable pageable = PageRequest.of(0, 5);

      Page<EventInfoDTO> result = eventService.getUserEventList(user, pageable);

      assertNotNull(result);
      System.out.println("사용자 신청 행사 수: " + result.getTotalElements());
      result.getContent().forEach(e -> System.out.println(" - " + e.getEventName()));
  }

  // 5-1. 특정 사용자의 신청 목록 조회 (예외 및 상세 로그 포함)
  //@Test
  @DisplayName("5. 특정 사용자의 신청 목록 조회 (자세히 보기)")
  @Transactional
  @Rollback(false)
  void testGetUserEventList1() {
      System.out.println("[5-1] 특정 사용자 신청 목록 상세 테스트 시작");
      Pageable pageable = PageRequest.of(0, 5);
      try {
          Page<EventInfoDTO> result = eventService.getUserEventList(user, pageable);
          System.out.println("사용자 신청 행사 수: " + result.getTotalElements());

          result.getContent().forEach(e -> {
              System.out.println(" - 행사명: " + e.getEventName());
              System.out.println("   모집대상: " + e.getCategory());
              System.out.println("   신청기간: " + e.getApplyStartPeriod() + " ~ " + e.getApplyEndPeriod());
              System.out.println("   상태: " + e.getState());
          });

          assertNotNull(result);
      } catch (Exception e) {
          e.printStackTrace();
          fail("예외 발생: " + e.getMessage());
      }
  }

  // 6. 관리자 행사 목록 검색 테스트
  //@Test
  @DisplayName("6. 관리자 행사 목록 검색 테스트")
  void testSearchAdminEventList() {
      System.out.println("[6] 관리자 행사 목록 검색 테스트 시작");
      Pageable pageable = PageRequest.of(0, 10);
      Page<EventInfoDTO> result = eventService.searchAdminEventList(pageable, "eventName", "", null);
      System.out.println("관리자 검색 결과 수: " + result.getTotalElements());
      result.getContent().forEach(e -> System.out.println(" - " + e.getEventName()));
      assertNotNull(result);
  }

  // 7. 특정 행사 신청자 목록 조회
  @Test
  @DisplayName("7. 특정 행사 신청자 목록 조회")
  void testGetApplicantsByEvent() {
      System.out.println("[7] 신청자 목록 조회 테스트 시작");

      // 1. 행사 등록
      EventInfoDTO dto = EventInfoDTO.builder()
              .eventName("신청자 테스트 " + System.currentTimeMillis()) // ✅ 매번 고유한 이름 사용
              .eventInfo("신청자용")
              .place("신청장소")
              .category(EventCategory.USER)
              .applyStartPeriod(LocalDateTime.now().minusDays(1))
              .applyEndPeriod(LocalDateTime.now().plusDays(3))
              .eventStartPeriod(LocalDateTime.now().plusDays(5))
              .eventEndPeriod(LocalDateTime.now().plusDays(10))
              .maxCapacity(10)
              .daysOfWeek(List.of(1))
              .build();

      MockMultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", "<<data>>".getBytes());
      eventService.registerEvent(dto, file);

      // 2. 방금 등록한 행사 번호 조회
      Long eventNum = eventService.getAllEvents().stream()
              .filter(e -> e.getEventName().startsWith("신청자 테스트"))
              .findFirst()
              .orElseThrow().getEventNum();

      // 3. 신청 시 예외 발생 방지
      try {
          eventService.applyEvent(EventApplyRequestDTO.builder()
                  .eventNum(eventNum)
                  .memId(user.getMemId())
                  .build());
      } catch (IllegalStateException e) {
          if (e.getMessage().contains("이미 신청한 프로그램")) {
              System.out.println("이미 신청된 상태입니다. 신청 스킵");
          } else {
              throw e;
          }
      }

      // 4. 신청자 목록 조회
      List<EventUseDTO> applicants = eventService.getApplicantsByEvent(eventNum);
      System.out.println("신청자 수: " + applicants.size());
      applicants.forEach(a -> System.out.println(" - " + a.getName() + " (" + a.getMemId() + ")"));

      assertFalse(applicants.isEmpty());
  }

  // 8. 사용자 ID로 신청목록 페이징 조회
  //@Test
  @DisplayName("8. 사용자 ID로 신청목록 페이징 조회")
  void testGetUseListByMemberPaged() {
      System.out.println("[8] 사용자 신청목록 페이징 조회 시작");
      Pageable pageable = PageRequest.of(0, 5);
      Page<EventUseDTO> result = eventService.getUseListByMemberPaged(user.getMemId(), pageable);
      System.out.println("신청목록 수: " + result.getTotalElements());
      result.getContent().forEach(u -> System.out.println(" - " + u.getEventName() + " (" + u.getRevState() + ")"));
      assertNotNull(result);
  }

  // 9. 종료되지 않은 이벤트 목록 조회
  //@Test
  @DisplayName("9. 종료되지 않은 이벤트 목록 조회")
  void testSearchNotEndedEventList() {
      System.out.println("[9] 종료되지 않은 이벤트 목록 조회 시작");

      try {
          List<EventInfoDTO> ongoingEvents = eventService.searchNotEndEventList();

          System.out.println("진행 중인 이벤트 수: " + ongoingEvents.size());
          ongoingEvents.forEach(e ->
              System.out.println(" - " + e.getEventName() + " | 종료일: " + e.getEventEndPeriod())
          );

          assertNotNull(ongoingEvents);
      } catch (Exception e) {
          e.printStackTrace();
          fail("예외 발생: " + e.getMessage()); // 이 줄이 없으면 테스트는 성공한 것으로 처리됨
      }
  }

  // 10. 종료되지 않은 이벤트 목록 상세 테스트
  //@Test
  @DisplayName("10. 종료되지 않은 이벤트 목록 상세 테스트")
  @Transactional
  @Rollback
  void testSearchNotEndedEventList2() {
      System.out.println("[10] 종료되지 않은 이벤트 등록 + 조회 테스트 시작");

      try {
          // given
          EventInfo event = EventInfo.builder()
                  .eventName("테스트 행사")
                  .eventInfo("행사 상세 설명입니다.")
                  .eventStartPeriod(LocalDateTime.now().plusDays(1))
                  .eventEndPeriod(LocalDateTime.now().plusDays(10))
                  .applyStartPeriod(LocalDateTime.now().minusDays(5))
                  .applyEndPeriod(LocalDateTime.now().plusDays(1))
                  .daysOfWeek(List.of(DayOfWeek.MONDAY.getValue(), DayOfWeek.WEDNESDAY.getValue(), DayOfWeek.FRIDAY.getValue()))
                  .place("테스트장소")
                  .category(EventCategory.USER)
                  .maxCapacity(30)
                  .originalName("test.pdf")
                  .filePath("/test/path")
                  .build();

          eventInfoRepository.save(event);

          // when
          List<EventInfoDTO> ongoingEvents = eventService.searchNotEndEventList();

          // then
          System.out.println("진행 중인 이벤트 수: " + ongoingEvents.size());
          ongoingEvents.forEach(e ->
              System.out.println(" - " + e.getEventName() + " | 종료일: " + e.getEventEndPeriod())
          );

          assertNotNull(ongoingEvents);
          assertTrue(ongoingEvents.size() > 0);
      } catch (Exception e) {
          e.printStackTrace();
          fail("예외 발생: " + e.getMessage());
      }
  }
}