package com.EduTech.service.facility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.facility.FacilityDetailDTO;
import com.EduTech.dto.facility.FacilityListDTO;
import com.EduTech.dto.facility.FacilityRegisterDTO;
import com.EduTech.dto.facility.FacilityReserveAdminDTO;
import com.EduTech.dto.facility.FacilityReserveApproveRequestDTO;
import com.EduTech.dto.facility.FacilityReserveListDTO;
import com.EduTech.dto.facility.FacilityReserveRequestDTO;
import com.EduTech.dto.facility.HolidayDayDTO;
import com.EduTech.dto.facility.ReservedBlockDTO;
import com.EduTech.entity.facility.FacilityState;

public interface FacilityService {

    // 시설 추가
    void registerFacility(FacilityRegisterDTO dto, List<MultipartFile> images);
    
    // 시설 조회
    Page<FacilityListDTO> getFacilityList(Pageable pageable, String keyword);

    // 시설 상세 (이름으로 조회 유지 OK)
    FacilityDetailDTO getFacilityDetail(Long facRevNum);

    // 예약 신청
    void reserveFacility(FacilityReserveRequestDTO requestDTO);

    // 예약 가능 여부
    boolean isReservable(Long facRevNum, LocalDate date, LocalTime start, LocalTime end);
    
    // 예약중인 시간 확인(사용)
    List<ReservedBlockDTO> getReservedBlocks(Long facRevNum, LocalDate date);

    // 내 예약 목록
    List<FacilityReserveListDTO> getMyReservations(String memId);

    // 관리자 목록
    List<FacilityReserveAdminDTO> getReservationsForAdmin(FacilityState state, LocalDate from, LocalDate to);

    // 관리자: 승인/거절 (reserveId 기준)
    boolean updateReservationState(FacilityReserveApproveRequestDTO approveRequest);

    // 예약 취소 (reserveId 기준으로!)
    boolean cancelReservation(Long reserveId, String requesterId);

    // 휴무일(사용)
//    boolean isHoliday(@Nullable Long facRevNum, LocalDate date);
    
    List<HolidayDayDTO> getHolidayDates(@Nullable Long facRevNum);

    // 휴무일 등록/삭제(사용)
    void registerHoliday(HolidayDayDTO dto);
    
    void deletePublicHolidayByDate(LocalDate date);
}
