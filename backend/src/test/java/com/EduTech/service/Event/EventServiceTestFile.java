package com.EduTech.service.Event;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventUseRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.event.EventServiceImpl;
import com.EduTech.util.FileUtil;

@SpringBootTest
@Transactional
public class EventServiceTestFile {

    @Autowired
    private EventServiceImpl eventService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EventInfoRepository infoRepository;

    @Autowired
    private EventUseRepository useRepository;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private FileUtil fileUtil;

    private Member member;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 정보 생성
        member = Member.builder()
                .memId("testUser")
                .pw("1234")
                .name("홍길동")
                .gender(MemberGender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .phone("010231234678")
                .addr("서울시 테스트구")
                .email("hong@test.com")
                .checkSms(true)
                .checkEmail(true)
                .role(MemberRole.USER)
                .state(MemberState.NORMAL)
                .build();

        memberRepository.save(member);
    }

    @Test
    @DisplayName("1. setFileInfo: 파일 업로드 및 정보 설정 정상 작동 여부")
    @Rollback
    void testSetFileInfo() {
        System.out.println("[1] 파일 업로드 및 정보 설정 테스트 시작");

        // given
        EventInfo info = EventInfo.builder()
                .eventName("파일테스트행사")
                .eventInfo("파일 업로드용 설명")
                .place("테스트장소")
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(2))
                .eventStartPeriod(LocalDateTime.now().plusDays(3))
                .eventEndPeriod(LocalDateTime.now().plusDays(4))
                .category(EventCategory.USER)
                .maxCapacity(50)
                .daysOfWeek(List.of(1, 3))
                .build();

        MockMultipartFile file = new MockMultipartFile(
                "file", "test.pdf", "application/pdf", "<<data>>".getBytes());

        // when: FileUtil의 동작을 mock 처리
        when(fileUtil.saveFiles(anyList(), anyString()))
                .thenReturn(List.of(Map.of(
                        "originalName", "test.pdf",
                        "filePath", "uploads/event/test.pdf"
                )));

        try {
            Method method = EventServiceImpl.class.getDeclaredMethod(
                    "setFileInfo", EventInfo.class, org.springframework.web.multipart.MultipartFile.class);
            method.setAccessible(true);
            method.invoke(eventService, info, file);

            // then
            System.out.println(" - 설정된 원본 파일 이름: " + info.getOriginalName());
            System.out.println(" - 저장된 파일 경로: " + info.getFilePath());

            assertAll("파일 정보 설정 확인",
                () -> assertNotNull(info.getOriginalName(), "원본 파일명이 null이면 안 됩니다."),
                () -> assertNotNull(info.getFilePath(), "파일 경로가 null이면 안 됩니다."),
                () -> assertEquals("test.pdf", info.getOriginalName(), "파일명이 예상과 다릅니다."),
                () -> assertEquals("uploads/event/test.pdf", info.getFilePath(), "파일 경로가 예상과 다릅니다.")
            );
        } catch (ReflectiveOperationException e) {
            fail("리플렉션 호출 중 오류 발생: " + e.getMessage(), e);
        }
    }
}
