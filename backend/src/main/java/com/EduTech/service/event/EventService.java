package com.EduTech.service.event;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.event.EventApplyRequestDTO;
import com.EduTech.dto.event.EventBannerDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.event.EventUseDTO;
import com.EduTech.entity.event.EventFile;
import com.EduTech.entity.member.Member;

public interface EventService {
	
	List<EventBannerDTO> getAllBanners(); 	// 배너 list 조회

	List<EventInfoDTO> getAllEvent();	 	// 행사 list 조회

	EventInfoDTO getEvent(Long eventNum);	// 행사 상세 조회

	EventInfoDTO getEventEntity(Long eventNum);	// 행사 정보 조회

	void registerBanner(EventBannerDTO dto, MultipartFile file); // 배너 등록

	void deleteBanner(Long evtFileNum); 	// 배너 삭제
	
	void registerEvent(EventInfoDTO dto, MultipartFile file); // 행사 등록(파일 1개 포함)

	void updateEvent(Long eventNum, EventInfoDTO dto,  MultipartFile file); // 행사 수정

	void deleteEvent(Long eventNum); // 행사 삭제

	void applyEvent(EventApplyRequestDTO dto);	// 사용자가 특정 행사 신청

	void cancelEvent(Long evtRevNum); // 사용자 신청 취소
	
	boolean isAlreadyApplied(Long eventNum, String memId); // 이미 신청 했는지 여부 확인

	boolean isAvailable(Long eventNum); // 신청 가능 여부(프론트에서 버튼 비활성화용)

	boolean isAllEventOccupied(EventInfoDTO request); // 모든 행사가 예약되었는지 확인

	boolean isEventAvailable(EventInfoDTO request);	 // 특정 행사 신청 가능 여부
	
	// 사용 가능한 강의실 목록 조회
	Map<String, Boolean> getEventAvailabilityStatus(EventInfoDTO dto);

	// 행사 목록 조회
	Page<EventInfoDTO> getEventList(Pageable pageable, String title, String eventInfo, String state);

	// 사용자용 목록 검색 조회
	Page<EventInfoDTO> searchEventList(Pageable pageable, String option, String query, String state);

	// 사용자용 행사목록 페이지 형태로 조회
	Page<EventInfoDTO> getUserEventList(Member member, Pageable pageable);

	// 관리자용 목록 조회
	Page<EventInfoDTO> searchAdminEventList(Pageable pageable, String option, String query, String state);

	// 사용자 신청 리스트
	Page<EventUseDTO> getUseListByMemberPaged(String memId, Pageable pageable);

	// 관리자용: 특정 프로그램의 신청자 목록 조회
	List<EventUseDTO> getApplicantsByEvent(Long eventNum);
		
	//강의 기간이 끝나지않은 모든 프로그램
	public List<EventInfoDTO> searchNotEndEventList();

}