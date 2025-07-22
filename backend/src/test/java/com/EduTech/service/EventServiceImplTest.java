package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.entity.event.Eventold;
import com.EduTech.entity.event.EventReserve;
import com.EduTech.entity.event.EventState;
import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.repository.event.EventReserveRepository;
import com.EduTech.service.event.EventServiceImplold;
import com.EduTech.util.FileUtil;

public class EventServiceImplTest {

    private EventInfoRepository infoRepository;
    private EventReserveRepository reserveRepository;
    private EventBannerRepository bannerRepository;
    private FileUtil fileUtil;
    private ModelMapper modelMapper;

    private EventServiceImplold eventService;

    @BeforeEach
    void setUp() {
        infoRepository = mock(EventInfoRepository.class);
        reserveRepository = mock(EventReserveRepository.class);
        bannerRepository = mock(EventBannerRepository.class);
        fileUtil = mock(FileUtil.class);
        modelMapper = new ModelMapper();

        eventService = new EventServiceImplold(
                bannerRepository,
                infoRepository,
                reserveRepository,
                fileUtil,
                modelMapper
        );
    }

    @Test
    void testApplyEvent_success() {
        // given
        Long eventId = 1L;
        String memId = "user001";
        EventApplyRequestDTO dto = new EventApplyRequestDTO();
        dto.setEventNum(eventId);
        dto.setMemId(memId);

        Eventold event = Eventold.builder()
                .eventNum(eventId)
                .eventName("테스트 행사")
                .currCapacity(1)
                .totCapacity(10)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(1))
                .state(EventState.신청중)
                .build();

        when(infoRepository.findById(eventId)).thenReturn(Optional.of(event));
        when(reserveRepository.existsByEvent_eventNumAndMember_memId(eventId, memId)).thenReturn(false);

        // when & then
        assertDoesNotThrow(() -> eventService.applyEvent(dto));
        verify(reserveRepository).save(any(EventReserve.class));
    }

    @Test
    void testIsAlreadyApplied_true() {
        // given
        Long eventId = 2L;
        String memId = "user002";

        when(reserveRepository.existsByEvent_eventNumAndMember_memId(eventId, memId)).thenReturn(true);

        // when
        boolean result = eventService.isAlreadyApplied(eventId, memId);

        // then
        assertTrue(result);
    }

    @Test
    void testIsAlreadyApplied_false() {
        // given
        Long eventId = 3L;
        String memId = "user003";

        when(reserveRepository.existsByEvent_eventNumAndMember_memId(eventId, memId)).thenReturn(false);

        // when
        boolean result = eventService.isAlreadyApplied(eventId, memId);

        // then
        assertFalse(result);
    }

    @Test
    void testIsAvailable_true() {
        // given
        Long eventId = 4L;
        Eventold event = Eventold.builder()
                .eventNum(eventId)
                .eventName("진행중 행사")
                .currCapacity(5)
                .totCapacity(10)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(1))
                .state(EventState.신청중)
                .build();

        when(infoRepository.findById(eventId)).thenReturn(Optional.of(event));

        // when
        boolean result = eventService.isAvailable(eventId);

        // then
        assertTrue(result);
    }

    @Test
    void testIsAvailable_false_dueToDate() {
        // given
        Long eventId = 5L;
        Eventold event = Eventold.builder()
                .eventNum(eventId)
                .currCapacity(1)
                .totCapacity(10)
                .applyStartPeriod(LocalDateTime.now().minusDays(5))
                .applyEndPeriod(LocalDateTime.now().minusDays(1))
                .state(EventState.신청마감)
                .build();

        when(infoRepository.findById(eventId)).thenReturn(Optional.of(event));

        // when
        boolean result = eventService.isAvailable(eventId);

        // then
        assertFalse(result);
    }

    @Test
    void testIsAvailable_false_dueToCapacity() {
        // given
        Long eventId = 6L;
        Eventold event = Eventold.builder()
                .eventNum(eventId)
                .currCapacity(10)
                .totCapacity(10)
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(1))
                .state(EventState.신청중)
                .build();

        when(infoRepository.findById(eventId)).thenReturn(Optional.of(event));

        // when
        boolean result = eventService.isAvailable(eventId);

        // then
        assertFalse(result);
    }
}