package com.EduTech.service.facility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.EduTech.dto.facility.FacilityDetailDTO;
import com.EduTech.dto.facility.FacilityHolidayDTO;
import com.EduTech.dto.facility.FacilityImageDTO;
import com.EduTech.dto.facility.FacilityReserveAdminDTO;
import com.EduTech.dto.facility.FacilityReserveApproveRequestDTO;
import com.EduTech.dto.facility.FacilityReserveListDTO;
import com.EduTech.dto.facility.FacilityReserveRequestDTO;
import com.EduTech.dto.facility.FacilityTimeDTO;
import com.EduTech.entity.facility.Facility;
import com.EduTech.entity.facility.FacilityHoliday;
import com.EduTech.entity.facility.FacilityReserve;
import com.EduTech.entity.facility.FacilityState;
import com.EduTech.repository.facility.FacilityHolidayRepository;
import com.EduTech.repository.facility.FacilityImageRepository;
import com.EduTech.repository.facility.FacilityRepository;
import com.EduTech.repository.facility.FacilityReserveRepository;
import com.EduTech.repository.facility.FacilityTimeRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepository;
    private final FacilityImageRepository facilityImageRepository;
    private final FacilityTimeRepository facilityTimeRepository;
    private final FacilityReserveRepository facilityReserveRepository;
    private final FacilityHolidayRepository facilityHolidayRepository;

    // 시설 상세 정보 조회 (이미지 포함)
    @Override
    public FacilityDetailDTO getFacilityDetail(String facName) {
        Facility facility = facilityRepository.findByFacName(facName)
                .orElseThrow(() -> new RuntimeException("시설 정보를 찾을 수 없습니다."));

        List<FacilityImageDTO> imageDTOs = facilityImageRepository.findByFacility_FacilityNum(facility.getFacilityNum())
                .stream()
                .map(img -> {
                    FacilityImageDTO dto = new FacilityImageDTO();
                    dto.setFacImageNum(img.getFacImageNum());
                    dto.setImageName(img.getImageName());
                    dto.setImageUrl(img.getImageUrl());
                    return dto;
                }).collect(Collectors.toList());

        FacilityDetailDTO dto = new FacilityDetailDTO();
        dto.setFacName(facility.getFacName());
        dto.setFacInfo(facility.getFacInfo());
        dto.setCapacity(facility.getCapacity());
        dto.setFacItem(facility.getFacItem());
        dto.setEtc(facility.getEtc());
        dto.setAvailableTime("09:00 ~ 18:00");
        dto.setImages(imageDTOs);

        return dto;
    }

    // 예약 가능 시간대 조회
    @Override
    public List<FacilityTimeDTO> getAvailableTimes(Long facilityNum, LocalDate date) {
        return facilityTimeRepository.findByFacility_FacilityNumAndFacDate(facilityNum, date)
                .stream()
                .map(t -> {
                    FacilityTimeDTO dto = new FacilityTimeDTO();
                    dto.setFacDate(t.getFacDate());
                    dto.setStartTime(t.getStartTime());
                    dto.setEndTime(t.getEndTime());
                    dto.setAvailable(t.isAvailable());
                    return dto;
                }).collect(Collectors.toList());
    }

    // 예약 가능 여부 판단
    @Override
    public boolean isReservable(Long facilityNum, LocalDate date, LocalTime start, LocalTime end) {
        if (LocalDateTime.of(date, end).isBefore(LocalDateTime.now())) return false;
        if (isHoliday(facilityNum, date)) return false;
        return !facilityReserveRepository.existsByFacility_FacilityNumAndFacDateAndStartTimeLessThanAndEndTimeGreaterThanAndStateIn(
                facilityNum, date, end, start, List.of(FacilityState.WAITING, FacilityState.APPROVED));
    }

    // 예약 신청 처리
    @Override
    @Transactional
    public void reserveFacility(FacilityReserveRequestDTO requestDTO) {
        Facility facility = facilityRepository.findByFacName(requestDTO.getFacName())
                .orElseThrow(() -> new RuntimeException("시설 정보가 존재하지 않습니다."));

        if (!isReservable(facility.getFacilityNum(), requestDTO.getFacDate(), requestDTO.getStartTime(), requestDTO.getEndTime())) {
            throw new IllegalStateException("해당 시간은 이미 예약되어 있거나 예약할 수 없습니다.");
        }

        FacilityReserve reserve = new FacilityReserve();
        reserve.setFacility(facility);
        reserve.setFacDate(requestDTO.getFacDate());
        reserve.setStartTime(requestDTO.getStartTime());
        reserve.setEndTime(requestDTO.getEndTime());
        reserve.setMemId(requestDTO.getMemId());
        reserve.setReserveAt(LocalDateTime.now());
        reserve.setState(FacilityState.WAITING);

        facilityReserveRepository.save(reserve);
    }

    // 내 예약 목록 조회
    @Override
    public List<FacilityReserveListDTO> getMyReservations(String memId) {
        return facilityReserveRepository.findByMemIdOrderByReserveAtDesc(memId)
                .stream()
                .map(r -> {
                    FacilityReserveListDTO dto = new FacilityReserveListDTO();
                    dto.setFacRevNum(r.getFacRevNum());
                    dto.setFacName(r.getFacility().getFacName());
                    dto.setFacDate(r.getFacDate());
                    dto.setStartTime(r.getStartTime());
                    dto.setEndTime(r.getEndTime());
                    dto.setState(r.getState());
                    return dto;
                }).collect(Collectors.toList());
    }

    // 관리자 예약 목록 조회
    @Override
    public List<FacilityReserveAdminDTO> getReservationsForAdmin(FacilityState state, LocalDate from, LocalDate to) {
        return facilityReserveRepository.searchForAdmin(state, from, to)
                .stream()
                .map(r -> {
                    FacilityReserveAdminDTO dto = new FacilityReserveAdminDTO();
                    dto.setFacRevNum(r.getFacRevNum());
                    dto.setFacName(r.getFacility().getFacName());
                    dto.setMemId(r.getMemId());
                    dto.setFacDate(r.getFacDate());
                    dto.setStartTime(r.getStartTime());
                    dto.setEndTime(r.getEndTime());
                    dto.setState(r.getState());
                    dto.setReserveAt(r.getReserveAt());
                    return dto;
                }).collect(Collectors.toList());
    }

    // 관리자 승인/거절 처리
    @Override
    @Transactional
    public boolean updateReservationState(FacilityReserveApproveRequestDTO approveRequest) {
        int updated = facilityReserveRepository.updateStateIfCurrent(
                approveRequest.getFacRevNum(),
                approveRequest.getState(),
                FacilityState.WAITING);
        return updated > 0;
    }
    
    // 사용자/관리자 에약 취소 처리 
    @Override
    @Transactional
    public boolean cancelReservation(Long facRevNum, boolean isAdmin, String requesterId) {
        FacilityReserve reserve = facilityReserveRepository.findById(facRevNum)
            .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        // 사용자일 경우 본인 예약만 취소 가능
        if (!isAdmin && !reserve.getMemId().equals(requesterId)) {
            throw new SecurityException("본인의 예약만 취소할 수 있습니다.");
        }

        // 이미 취소/거절된 예약은 중복 처리 불가
        if (reserve.getState() == FacilityState.CANCELLED || reserve.getState() == FacilityState.REJECTED) {
            throw new IllegalStateException("이미 취소되었거나 처리된 예약입니다.");
        }

        // 시작 시간 지난 예약은 취소 불가 (선택적 조건)
        if (LocalDateTime.of(reserve.getFacDate(), reserve.getStartTime()).isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("이미 시작된 예약은 취소할 수 없습니다.");
        }

        // 상태 변경
        reserve.setState(FacilityState.CANCELLED);
        return true;
    }

    // 휴무일 여부 확인
    @Override
    public boolean isHoliday(Long facilityNum, LocalDate date) {
        return facilityHolidayRepository.existsByFacility_FacilityNumAndHolidayDate(facilityNum, date);
    }

    // 휴무일 리스트 조회
    @Override
    public List<LocalDate> getHolidayDates(Long facilityNum) {
        return facilityHolidayRepository.findByFacility_FacilityNum(facilityNum)
                .stream()
                .map(FacilityHoliday::getHolidayDate)
                .collect(Collectors.toList());
    }

    // 휴무일 등록
    @Override
    @Transactional
    public void registerHoliday(FacilityHolidayDTO dto) {
        if (facilityHolidayRepository.existsByFacility_FacilityNumAndHolidayDate(dto.getFacilityNum(), dto.getHolidayDate())) {
            throw new IllegalStateException("이미 등록된 휴무일입니다.");
        }

        Facility facility = facilityRepository.findById(dto.getFacilityNum())
                .orElseThrow(() -> new RuntimeException("해당 시설을 찾을 수 없습니다."));

        FacilityHoliday holiday = new FacilityHoliday();
        holiday.setFacility(facility);
        holiday.setHolidayDate(dto.getHolidayDate());
        holiday.setReason(dto.getReason());

        facilityHolidayRepository.save(holiday);
    }

    // 휴무일 삭제
    @Override
    @Transactional
    public void deleteHoliday(Long holidayId) {
        FacilityHoliday holiday = facilityHolidayRepository.findById(holidayId)
                .orElseThrow(() -> new RuntimeException("휴무일 정보를 찾을 수 없습니다"));
        facilityHolidayRepository.delete(holiday);
    }
}