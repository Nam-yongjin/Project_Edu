package com.EduTech.service.event;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventBannerDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.entity.member.Member;

public interface EventService {

    // =============================
    // 1. 이벤트 조회
    // =============================

    List<EventInfoDTO> getAllEvents(); // 전체 이벤트 조회
    
    Page<EventInfoDTO> getEventList(Pageable pageable, String title, String eventInfo, EventState state); // 관리자/운영자용
    
    Page<EventInfoDTO> searchEventList(Pageable pageable, String option, String query, EventState state); // 사용자 검색
    
    Page<EventInfoDTO> getUserEventList(Member member, Pageable pageable); // 사용자의 이벤트 목록
    
    Page<EventInfoDTO> searchAdminEventList(Pageable pageable, String option, String query, EventState state); // 관리자 검색 목록
    
    EventInfoDTO getEvent(Long eventNum); // 이벤트 상세 조회
    
    EventInfo getEventEntity(Long eventNum); // 내부용 Entity 직접 조회

    List<EventInfoDTO> searchNotEndedEventList(); // 진행 중인 이벤트만 조회

    // =============================
    // 2. 이벤트 등록/수정/삭제
    // =============================

    void registerEvent(EventInfoDTO dto, MultipartFile file); // 파일 포함 등록
    
    void updateEvent(Long eventNum, EventInfoDTO dto, MultipartFile file); // 수정
    
    void deleteEvent(Long eventNum); // 삭제

    // =============================
    // 3. 배너 관련
    // =============================

    List<EventBannerDTO> getAllBanners(); // 배너 목록 조회
    
    void registerBanner(EventBannerDTO dto, MultipartFile file); // 배너 등록
    
    void deleteBanner(Long evtFileNum); // 배너 삭제

    // =============================
    // 4. 사용자 신청 관련
    // =============================

    void applyEvent(EventApplyRequestDTO dto); // 이벤트 신청
    
    void cancelEvent(Long eventUseNo); // 신청 취소

    boolean isAlreadyApplied(Long eventNum, String memId); // 중복 신청 여부
    
    boolean isAvailable(Long eventNum); // 신청 가능 여부

    // =============================
    // 5. 신청 내역 및 관리
    // =============================

    Page<EventUseDTO> getUseListByMemberPaged(String memId, Pageable pageable); // 사용자 신청 리스트
    
    List<EventUseDTO> getApplicantsByEvent(Long eventNum); // 특정 이벤트 신청자 목록 (관리자용)

}
