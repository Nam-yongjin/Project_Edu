package com.EduTech.service.facility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.facility.FacilityDetailDTO;
import com.EduTech.dto.facility.FacilityHolidayDTO;
import com.EduTech.dto.facility.FacilityRegisterDTO;
import com.EduTech.dto.facility.FacilityReserveAdminDTO;
import com.EduTech.dto.facility.FacilityReserveApproveRequestDTO;
import com.EduTech.dto.facility.FacilityReserveListDTO;
import com.EduTech.dto.facility.FacilityReserveRequestDTO;
import com.EduTech.dto.facility.FacilityTimeDTO;
import com.EduTech.entity.facility.FacilityState;

public interface FacilityService {

    // 시설 추가
    void registerFacility(FacilityRegisterDTO dto, List<MultipartFile> images);

    // 시설 상세 (이름으로 조회 유지 OK)
    FacilityDetailDTO getFacilityDetail(String facName);

    // 예약 가능 시간대 (파라명 통일: facRevNum)
    List<FacilityTimeDTO> getAvailableTimes(Long facRevNum, LocalDate date);

    // 예약 신청
    void reserveFacility(FacilityReserveRequestDTO requestDTO);

    // 예약 가능 여부
    boolean isReservable(Long facRevNum, LocalDate date, LocalTime start, LocalTime end);

    // 내 예약 목록
    List<FacilityReserveListDTO> getMyReservations(String memId);

    // 관리자 목록
    List<FacilityReserveAdminDTO> getReservationsForAdmin(FacilityState state, LocalDate from, LocalDate to);

    // 관리자: 승인/거절 (reserveId 기준)
    boolean updateReservationState(FacilityReserveApproveRequestDTO approveRequest);

    // 예약 취소 (reserveId 기준으로!)
    boolean cancelReservation(Long reserveId, String requesterId);

    // 휴무일
    boolean isHoliday(Long facRevNum, LocalDate date);
    List<LocalDate> getHolidayDates(Long facRevNum);

    // 휴무일 등록/삭제
    void registerHoliday(FacilityHolidayDTO dto);
    void deleteHoliday(Long holidayId);
}
