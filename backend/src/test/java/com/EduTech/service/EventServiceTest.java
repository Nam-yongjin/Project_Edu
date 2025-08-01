package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.mock.web.MockMultipartFile;

import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.service.event.EventServiceImpl;
import com.EduTech.util.FileUtil;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventInfoRepository infoRepository;

    @Mock
    private FileUtil fileUtil;

    @Mock
    private ModelMapper modelMapper;

    private EventInfoDTO dto;
    private EventInfo entity;

    @BeforeEach
    void setup() {
        dto = EventInfoDTO.builder()
                .eventName("테스트 행사")
                .eventInfo("소개입니다")
                .maxCapacity(100)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(1))
                .eventStartPeriod(LocalDateTime.now().plusDays(2))
                .eventEndPeriod(LocalDateTime.now().plusDays(10))
                .place("강의실 1호")
                .build();

        entity = new EventInfo();
        entity.setEventName(dto.getEventName());
        entity.setEventInfo(dto.getEventInfo());
        entity.setMaxCapacity(dto.getMaxCapacity());
        entity.setApplyStartPeriod(dto.getApplyStartPeriod());
        entity.setApplyEndPeriod(dto.getApplyEndPeriod());
        entity.setEventStartPeriod(dto.getEventStartPeriod());
        entity.setEventEndPeriod(dto.getEventEndPeriod());
        entity.setPlace(dto.getPlace());
    }

    //@Test
    void testRegisterEvent_success() throws Exception {
        // given
        when(modelMapper.map(dto, EventInfo.class)).thenReturn(entity);
        when(infoRepository.save(any(EventInfo.class))).thenReturn(entity);

        MockMultipartFile mockImage = new MockMultipartFile(
                "imageList", "test.jpg", "image/jpeg", "dummy image content".getBytes());

        // when
        eventService.registerEvent(dto, List.of(mockImage), null);

        // then
        verify(infoRepository, times(1)).save(any(EventInfo.class));
    }

    @Test
    void testUpdateEvent_success() {
        // given
        Long eventNum = 1L;

        EventInfoDTO dto = EventInfoDTO.builder()
            .eventName("수정된 행사")
            .eventInfo("소개입니다")
            .place("강의실 1호")
            .maxCapacity(100)
            .applyStartPeriod(LocalDateTime.now())
            .applyEndPeriod(LocalDateTime.now().plusDays(2))
            .eventStartPeriod(LocalDateTime.now().plusDays(3))
            .eventEndPeriod(LocalDateTime.now().plusDays(10))
            .build();

        EventInfo entity = new EventInfo();
        entity.setEventNum(eventNum);

        when(infoRepository.findById(eventNum)).thenReturn(Optional.of(entity));
        when(modelMapper.map(eq(dto), eq(EventInfo.class))).thenReturn(entity); // ✅ 정확한 매칭

        MockMultipartFile file = new MockMultipartFile("file", "file.pdf", "application/pdf", "content".getBytes());

        // when & then
        assertDoesNotThrow(() -> eventService.updateEvent(eventNum, dto, file));
        verify(infoRepository, times(1)).save(any(EventInfo.class));
    }

    //@Test
    void testDeleteEvent_success() {
        // given
        Long eventNum = 1L;
        entity.setEventNum(eventNum);
        when(infoRepository.findById(eventNum)).thenReturn(Optional.of(entity));

        // when
        eventService.deleteEvent(eventNum);

        // then
        verify(infoRepository, times(1)).delete(entity);
    }
}
