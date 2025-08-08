package com.EduTech.service.event;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventSearchRequestDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventInfo;

public interface EventService {

    // ─────────────────────────────────────────────
    // 1. 행사 조회
    // ─────────────────────────────────────────────

    // 전체 행사 조회 (필터링 없이 페이징)
    List<EventInfoDTO> getAllEventsWithoutFilter(int page);

    // 조건 검색 (분류, 키워드 등)
    Page<EventInfoDTO> searchEventList(EventSearchRequestDTO dto, int page);

    // 행사 상세 조회
    EventInfoDTO getEvent(Long eventNum);

    // 이달의 OPEN 행사 목록 (배너용)
    List<EventInfoDTO> getbannerEvent();
    
    EventInfo getEventEntity(Long eventNum); // 내부용 Entity 직접 조회


    // ─────────────────────────────────────────────
    // 2. 행사 등록 / 수정 / 취소 (관리자)
    // ─────────────────────────────────────────────

    // 행사 등록 (파일 포함)
    void registerEvent(
        EventInfoDTO dto,
        MultipartFile mainImage,
        List<MultipartFile> imageList,
        MultipartFile mainFile,
        List<MultipartFile> attachList
    );

    // 행사 수정 (파일 포함)
    void updateEvent(
        Long eventNum,
        EventInfoDTO dto,
        MultipartFile mainImage,
        List<MultipartFile> imageList,
        MultipartFile mainFile,
        List<MultipartFile> attachList
    );

    // 행사 취소 처리
    void deleteEvent(Long eventNum);


    // ─────────────────────────────────────────────
    // 3. 사용자 신청 관련
    // ─────────────────────────────────────────────

    // 행사 신청
    void applyEvent(EventApplyRequestDTO dto);

    // 행사 신청 취소
    void cancelEvent(Long evtRevNum, String memId);

    // 중복 신청 여부 확인
    boolean isAlreadyApplied(Long eventNum, String memId);

    // 신청 가능 여부 확인 (기간 기준)
    boolean isAvailable(Long eventNum);


    // ─────────────────────────────────────────────
    // 4. 사용자 신청 내역
    // ─────────────────────────────────────────────

    // 사용자별 신청 내역 조회 (페이징)
    Page<EventUseDTO> getUseListByMemberPaged(String memId, Pageable pageable);
}
