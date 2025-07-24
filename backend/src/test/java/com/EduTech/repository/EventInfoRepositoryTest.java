package com.EduTech.repository;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.repository.event.EventInfoRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class EventInfoRepositoryTest {

    @Autowired
    EventInfoRepository eventInfoRepository;

    @Test
    @Transactional
    @Rollback(false)
    @DisplayName("프로그램 정보 등록")
    public void applyEventInfoTest() {
        EventInfo eventInfo = EventInfo.builder()
                .eventName("프로그램 생성1")
                .applyStartPeriod(LocalDateTime.of(2025, 5, 1, 14, 0))
                .applyEndPeriod(LocalDateTime.of(2025, 5, 8, 18, 0))
                .daysOfWeek(new ArrayList<>(List.of(
                	    DayOfWeek.MONDAY.getValue(),
                	    DayOfWeek.WEDNESDAY.getValue(),
                	    DayOfWeek.FRIDAY.getValue()
                	)))
                .place("장소")
                .eventStartPeriod(LocalDateTime.of(2025, 6, 1, 14, 0))
                .eventEndPeriod(LocalDateTime.of(2025, 6, 8, 18, 0))
                .category(EventCategory.USER) 
                .state(EventState.OPEN)
                .currCapacity(0) // 👉 추가 권장
                .maxCapacity(10)
                .originalName("야외독서 피크닉 강의계획서")
                .filePath("/event/picnic")
                .eventInfo("야외에서 독서하는 행사입니다")
                .build();

        EventInfo saved = eventInfoRepository.save(eventInfo);
        System.out.println("프로그램 요일: " + saved.getDaysOfWeek());
        System.out.println("등록된 프로그램명: " + saved.getEventName());
    }

    
    @Test
    @Transactional
    @DisplayName("프로그램 정보 조회")
    public void findEventInfoInfoTest() {
        // 테스트용 데이터 저장
        EventInfo eventInfo = EventInfo.builder()
                .eventName("조회 테스트")
                .applyStartPeriod(LocalDateTime.of(2025, 5, 2, 14, 0))
                .applyEndPeriod(LocalDateTime.of(2025, 5, 9, 18, 0))
                .daysOfWeek(List.of(
                    DayOfWeek.MONDAY.getValue(), 
                    DayOfWeek.WEDNESDAY.getValue(), 
                    DayOfWeek.FRIDAY.getValue()))
                .place("강의실 2")
                .eventStartPeriod(LocalDateTime.of(2025, 6, 2, 10, 0))
                .eventEndPeriod(LocalDateTime.of(2025, 6, 9, 12, 0))
                .category(EventCategory.USER) 
                .state(EventState.BEFORE) // ✅ 상태 필수
                .currCapacity(0)          // ✅ 현재 인원 필수
                .maxCapacity(20)
                .eventInfo("조회 테스트용 설명입니다") // ✅ 행사 설명 필수
                .originalName("조회계획서.pdf")
                .filePath("/event/query")
                .build();

        EventInfo saved = eventInfoRepository.save(eventInfo);
        Long memId = saved.getEventNum();

        EventInfo find = eventInfoRepository.findById(memId)
                .orElseThrow(() -> new RuntimeException("프로그램 정보를 찾을 수 없습니다."));

        System.out.println("조회한 프로그램명: " + find.getEventName());
        System.out.println("조회한 요일: " + find.getDaysOfWeek());
        System.out.println("조회한 설명: " + find.getEventInfo());
        System.out.println("조회한 상태: " + find.getState());
    }


    @Test
    @Transactional
    @DisplayName("프로그램 정보 수정")
    public void updateEventInfoTest() {
        EventInfo eventInfo = EventInfo.builder()
                .eventName("수정 전 프로그램")
                .applyStartPeriod(LocalDateTime.of(2025, 5, 3, 14, 0))
                .applyEndPeriod(LocalDateTime.of(2025, 5, 10, 18, 0))
                .daysOfWeek(new ArrayList<>(List.of(
                        DayOfWeek.MONDAY.getValue(),
                        DayOfWeek.WEDNESDAY.getValue(),
                        DayOfWeek.FRIDAY.getValue()
                ))) // 불변 컬렉션 제거
                .place("로비")
                .eventStartPeriod(LocalDateTime.of(2025, 6, 3, 9, 0))
                .eventEndPeriod(LocalDateTime.of(2025, 6, 10, 11, 0))
                .category(EventCategory.USER) 
                .state(EventState.OPEN)
                .maxCapacity(25)
                .originalName("수정계획서.docx")
                .filePath("/programs/update")
                .eventInfo("수정 전 설명입니다.")
                .build();

        EventInfo saved = eventInfoRepository.save(eventInfo);

        saved.setEventName("수정된 프로그램명");
        saved.setPlace("수정된 강의실");
        saved.setEventInfo("수정된 설명입니다.");
        
        // 수정한 객체 다시 저장
        EventInfo updated = eventInfoRepository.save(saved);

        System.out.println("수정된 프로그램명: " + updated.getEventName());
        System.out.println("수정된 장소: " + updated.getPlace());
    }
    
    @Test
    @Transactional
    @Rollback(false)
    @DisplayName("프로그램 정보 삭제")
    public void deleteProgramInfoTest() {
        // 1. 테스트용 프로그램 정보 생성
        EventInfo eventInfo = EventInfo.builder()
                .eventName("삭제 대상 프로그램")
                .applyStartPeriod(LocalDateTime.of(2025, 5, 4, 10, 0))
                .applyEndPeriod(LocalDateTime.of(2025, 5, 11, 18, 0))
                .daysOfWeek(List.of(
                    DayOfWeek.MONDAY.getValue(),
                    DayOfWeek.WEDNESDAY.getValue(),
                    DayOfWeek.FRIDAY.getValue()))
                .place("강의실 1")
                .eventStartPeriod(LocalDateTime.of(2025, 6, 4, 10, 0))
                .eventEndPeriod(LocalDateTime.of(2025, 6, 11, 10, 0))
                .category(EventCategory.USER) 
                .state(EventState.BEFORE)        // ✅ 필수
                .currCapacity(0)                 // ✅ 필수
                .eventInfo("삭제 테스트용 설명입니다") // ✅ 필수
                .maxCapacity(20)
                .originalName("삭제계획서.docx")
                .filePath("/programs/delete")
                .build();

        // 2. 저장
        EventInfo saved = eventInfoRepository.save(eventInfo);
        Long eventNum = saved.getEventNum();

        // 3. 삭제
        eventInfoRepository.deleteById(eventNum);

        // 4. 삭제 확인
        boolean exists = eventInfoRepository.findById(eventNum).isPresent();
        System.out.println("삭제 여부: " + (exists ? "❌ 실패" : "✅ 성공"));
    }

}