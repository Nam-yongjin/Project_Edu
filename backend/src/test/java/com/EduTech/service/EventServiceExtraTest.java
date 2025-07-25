package com.EduTech.service;

import static org.mockito.Mockito.mock;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventBannerDTO;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.member.Member;
import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventUseRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.service.event.EventServiceImpl;
import com.EduTech.util.FileUtil;

public class EventServiceExtraTest {

    // ================================
    // 테스트 대상 클래스 및 Mock 초기화
    // ================================

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventInfoRepository infoRepository;
    @Mock
    private EventUseRepository useRepository;
    @Mock
    private EventBannerRepository bannerRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private FileUtil fileUtil;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    // ================================
    // 파일 처리 관련 테스트
    // ================================

    // 허용되지 않은 확장자 파일 업로드 시 예외가 발생해야 함

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("malware.exe");
        EventInfoDTO dto = new EventInfoDTO();

        assertThrows(IllegalArgumentException.class, () -> eventService.registerEvent(dto, file));
    }

    // 파일이 null인 경우에도 setFileInfo가 예외 없이 처리되어야 함

        EventInfo info = new EventInfo();
        assertDoesNotThrow(() -> eventService.setFileInfo(info, null));
    }

    // ================================
    // 예외 상황 처리 테스트
    // ================================

    // 존재하지 않는 ID로 행사 수정 시 RuntimeException 발생 여부 확인

        when(infoRepository.findById(999L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> eventService.updateEvent(999L, new EventInfoDTO(), null));
    }

    // 배너 등록 시 파일이 null일 경우 예외 발생 테스트

        EventBannerDTO dto = new EventBannerDTO();
        assertThrows(IllegalArgumentException.class, () -> eventService.registerBanner(dto, null));
    }

    // 이미 신청된 행사에 중복 신청 시 IllegalStateException 발생 테스트

        when(useRepository.existsByEventInfo_EventNumAndMember_MemId(1L, "user1")).thenReturn(true);
        EventApplyRequestDTO dto = new EventApplyRequestDTO();
        dto.setEventNum(1L);
        dto.setMemId("user1");
        assertThrows(IllegalStateException.class, () -> eventService.applyEvent(dto));
    }

    // 신청 기간이 지난 행사에 신청 시 예외 발생 테스트

        EventInfo closedEvent = new EventInfo();
        closedEvent.setApplyStartPeriod(LocalDateTime.now().minusDays(10));
        closedEvent.setApplyEndPeriod(LocalDateTime.now().minusDays(1));
        closedEvent.setCategory(EventCategory.USER);
        when(infoRepository.findById(1L)).thenReturn(Optional.of(closedEvent));
        when(useRepository.existsByEventInfo_EventNumAndMember_MemId(1L, "user1")).thenReturn(false);

        EventApplyRequestDTO dto = new EventApplyRequestDTO();
        dto.setEventNum(1L);
        dto.setMemId("user1");

        assertThrows(IllegalStateException.class, () -> eventService.applyEvent(dto));
    }

    // null DTO로 행사 등록 시 NullPointerException 발생 테스트

        assertThrows(NullPointerException.class, () -> eventService.registerEvent(null, null));
    }

    // ================================
    // 상태 계산 로직 테스트
    // ================================

    // 신청 시작 전인 경우 상태가 BEFORE로 계산되는지 테스트

        EventState result = eventService.calculateState(LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        assertEquals(EventState.BEFORE, result);
    }

    // 신청 시작일과 종료일 사이일 경우 상태가 OPEN으로 계산되는지 테스트

        EventState result = eventService.calculateState(LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
        assertEquals(EventState.OPEN, result);
    }

    // 신청 종료일 이후일 경우 상태가 CLOSED로 계산되는지 테스트

        EventState result = eventService.calculateState(LocalDateTime.now().minusDays(3), LocalDateTime.now().minusDays(1));
        assertEquals(EventState.CLOSED, result);
    }

    // ================================
    // 모집 대상 권한 확인 테스트
    // ================================

    // 사용자 역할이 USER인 경우 USER 모집 대상에 신청 가능 여부 테스트

        Member member = new Member();
        member.setRole(MemberRole.USER);
        assertTrue(eventService.isEligible(EventCategory.USER, member));
    }

    // 사용자 역할이 STUDENT인 경우 STUDENT 모집 대상에 신청 가능 여부 테스트

        Member member = new Member();
        member.setRole(MemberRole.STUDENT);
        assertTrue(eventService.isEligible(EventCategory.STUDENT, member));
    }

    // 사용자 역할이 TEACHER인 경우 TEACHER 모집 대상에 신청 가능 여부 테스트

        Member member = new Member();
        member.setRole(MemberRole.TEACHER);
        assertTrue(eventService.isEligible(EventCategory.TEACHER, member));
    }

    // 사용자 정보가 null일 경우 신청 불가 처리 여부 테스트

        assertFalse(eventService.isEligible(EventCategory.USER, null));
    }
}
