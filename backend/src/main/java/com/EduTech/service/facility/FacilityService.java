package com.EduTech.service.facility;

import com.EduTech.dto.facility.*;
import com.EduTech.entity.facility.FacilityState;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

//시설 예약 관련 서비스 인터페이스
public interface FacilityService {

    // 시설 상세 정보 조회 (이미지 포함) 
    FacilityDetailDTO getFacilityDetail(String facName);

    // 특정 시설의 예약 가능 시간대 조회 (특정 날짜 기준)
    List<FacilityTimeDTO> getAvailableTimes(Long facilityNum, LocalDate date);

    // 시설 예약 신청 처리
    void reserveFacility(FacilityReserveRequestDTO requestDTO);

    // 예약 가능 여부 확인 (중복 예약, 시간 경과 등 체크 포함)
    boolean isReservable(Long facilityNum, LocalDate date, LocalTime start, LocalTime end);

    // 사용자 본인의 예약 목록 조회 (마이페이지용)
    List<FacilityReserveListDTO> getMyReservations(String memId);

    // 관리자용 예약 목록 조회 (필터: 상태 + 날짜 범위)
    List<FacilityReserveAdminDTO> getReservationsForAdmin(FacilityState state, LocalDate from, LocalDate to);

    // 관리자: 예약 승인 또는 거절 처리
    boolean updateReservationState(FacilityReserveApproveRequestDTO approveRequest);
    
    // 예약 취소 처리 (사용자 or 관리자)
    boolean cancelReservation(Long facRevNum, boolean isAdmin, String memId);

    // 해당 날짜가 휴무일인지 확인
    boolean isHoliday(Long facilityNum, LocalDate date);

    // 특정 시설의 휴무일 리스트 반환 (달력에 표시용)
    List<LocalDate> getHolidayDates(Long facilityNum);

    // 관리자: 휴무일 등록
    void registerHoliday(FacilityHolidayDTO dto);

    // 관리자: 휴무일 삭제
    void deleteHoliday(Long holidayId);
}