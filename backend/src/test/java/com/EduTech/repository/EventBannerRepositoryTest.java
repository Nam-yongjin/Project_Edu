package com.EduTech.repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import com.EduTech.entity.event.EventBanner;
import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.member.MemberRepository;

import jakarta.transaction.Transactional;

@SpringBootTest
public class EventBannerRepositoryTest {

    @Autowired
    private EventBannerRepository eventBannerRepository;

    @Autowired
    private EventInfoRepository eventInfoRepository;

    @Autowired
    private MemberRepository memberRepository;

    private EventInfo testEvent;

    @BeforeEach
    public void setup() {
        Member admin = Member.builder()
                .memId("admin01")
                .pw("1234")
                .name("관리자")
                .gender(MemberGender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .phone("01011112222")
                .addr("대구")
                .email("admin@test.com")
                .checkSms(true)
                .checkEmail(true)
                .role(MemberRole.USER)
                .state(MemberState.NORMAL)
                .build();
        memberRepository.save(admin);

        testEvent = EventInfo.builder()
                .eventName("배너 테스트 행사")
                .applyStartPeriod(LocalDateTime.of(2025, 5, 1, 10, 0))
                .applyEndPeriod(LocalDateTime.of(2025, 5, 31, 18, 0))
                .daysOfWeek(new ArrayList<>(List.of(DayOfWeek.TUESDAY.getValue())))
                .place("1층 강의실")
                .eventStartPeriod(LocalDateTime.of(2025, 6, 1, 10, 0))
                .eventEndPeriod(LocalDateTime.of(2025, 6, 30, 12, 0))
                .category(EventCategory.USER) 
                .maxCapacity(30)
                .currCapacity(0)
                .state(EventState.OPEN)
                .originalName("file.pdf")
                .filePath("/files/file.pdf")
                .eventInfo("배너 테스트용 행사입니다.")
                .build();

        eventInfoRepository.save(testEvent);
    }

    @Test
    @DisplayName("행사 배너 생성")
    @Transactional
    @Rollback(false)
    public void createEventBanner() {
        EventBanner banner = EventBanner.builder()
                .originalName("행사 배너")
                .filePath("/banner/event.jpg")
                .eventInfo(testEvent)
                .build();

        eventBannerRepository.save(banner);
    }

    @Test
    @DisplayName("행사 배너 조회")
    @Transactional
    @Rollback(false)
    public void findEventBanner() {
        EventBanner banner = EventBanner.builder()
                .originalName("조회용 배너")
                .filePath("/banner/read.jpg")
                .eventInfo(testEvent)
                .build();

        EventBanner saved = eventBannerRepository.save(banner);
        Long id = saved.getEvtFileNum();

        eventBannerRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("행사 배너를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("행사 배너 수정")
    @Transactional
    @Rollback(false)
    public void updateEventBanner() {
        EventBanner banner = EventBanner.builder()
                .originalName("수정 전 배너")
                .filePath("/banner/before.jpg")
                .eventInfo(testEvent)
                .build();

        EventBanner saved = eventBannerRepository.save(banner);

        saved.setOriginalName("수정 후 배너");
        saved.setFilePath("/banner/after.jpg");

        eventBannerRepository.save(saved);
    }

    @Test
    @DisplayName("행사 배너 삭제")
    @Transactional
    @Rollback(false)
    public void deleteEventBanner() {
        EventBanner banner = EventBanner.builder()
                .originalName("삭제 배너")
                .filePath("/banner/delete.jpg")
                .eventInfo(testEvent)
                .build();

        EventBanner saved = eventBannerRepository.save(banner);
        Long id = saved.getEvtFileNum();

        eventBannerRepository.deleteById(id);

        boolean exists = eventBannerRepository.findById(id).isPresent();
        System.out.println("삭제 여부: " + (exists ? "실패" : "성공"));
    }
}
