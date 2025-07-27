package com.EduTech.controller.facility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.facility.FacilityDetailDTO;
import com.EduTech.dto.facility.FacilityHolidayDTO;
import com.EduTech.dto.facility.FacilityReserveAdminDTO;
import com.EduTech.dto.facility.FacilityReserveApproveRequestDTO;
import com.EduTech.dto.facility.FacilityReserveListDTO;
import com.EduTech.dto.facility.FacilityReserveRequestDTO;
import com.EduTech.dto.facility.FacilityTimeDTO;
import com.EduTech.entity.facility.FacilityState;
import com.EduTech.service.facility.FacilityService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/facility")
@RequiredArgsConstructor
public class FacilityController {

    private final FacilityService facilityService;

    // 시설 상세 조회
    @GetMapping("/detail/{facName}")
    public ResponseEntity<FacilityDetailDTO> getFacilityDetail(@PathVariable String facName) {
        return ResponseEntity.ok(facilityService.getFacilityDetail(facName));
    }

    // 예약 가능 시간 조회
    @GetMapping("/times")
    public ResponseEntity<List<FacilityTimeDTO>> getAvailableTimes(
            @RequestParam Long facilityNum,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(facilityService.getAvailableTimes(facilityNum, date));
    }

    // 예약 신청
    @PostMapping("/reserve")
    public ResponseEntity<Void> reserveFacility(@RequestBody FacilityReserveRequestDTO dto) {
        facilityService.reserveFacility(dto);
        return ResponseEntity.ok().build();
    }

    // 예약 가능 여부 확인
    @GetMapping("/reservable")
    public ResponseEntity<Boolean> checkReservable(
            @RequestParam Long facilityNum,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime end
    ) {
        return ResponseEntity.ok(facilityService.isReservable(facilityNum, date, start, end));
    }

    // 사용자 예약 목록
    @GetMapping("/my-reservations")
    public ResponseEntity<List<FacilityReserveListDTO>> getMyReservations(@RequestParam String memId) {
        return ResponseEntity.ok(facilityService.getMyReservations(memId));
    }

    // 관리자 예약 목록
    @GetMapping("/admin/reservations")
    public ResponseEntity<List<FacilityReserveAdminDTO>> getReservationsForAdmin(
            @RequestParam(required = false) FacilityState state,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        return ResponseEntity.ok(facilityService.getReservationsForAdmin(state, from, to));
    }

    // 관리자 승인/거절 처리
    @PostMapping("/admin/approve")
    public ResponseEntity<Void> updateReservationState(@RequestBody FacilityReserveApproveRequestDTO dto) {
        facilityService.updateReservationState(dto);
        return ResponseEntity.ok().build();
    }
    
    // 사용자 예약 취소
    @PatchMapping("/my/reservation/{facRevNum}/cancel")
    public ResponseEntity<?> userCancel(
            @PathVariable Long facRevNum,
            @AuthenticationPrincipal CustomUserDetails user // 로그인 사용자
    ) {
        boolean success = facilityService.cancelReservation(facRevNum, false, user.getUsername());
        return ResponseEntity.ok(success ? "취소 완료" : "취소 실패");
    }
    
    //관치자 에약 취소
    @PatchMapping("/admin/reservation/{facRevNum}/cancel")
    public ResponseEntity<?> adminCancel(@PathVariable Long facRevNum) {
        boolean success = facilityService.cancelReservation(facRevNum, true, null);
        return ResponseEntity.ok(success ? "강제 취소 완료" : "취소 실패");
    }
    
    // 휴무일 등록
    @PostMapping("/admin/holiday")
    public ResponseEntity<Void> registerHoliday(@RequestBody FacilityHolidayDTO dto) {
        facilityService.registerHoliday(dto);
        return ResponseEntity.ok().build();
    }

    // 휴무일 삭제
    @DeleteMapping("/admin/holiday/{holidayId}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long holidayId) {
        facilityService.deleteHoliday(holidayId);
        return ResponseEntity.ok().build();
    }

    // 휴무일 목록 조회
    @GetMapping("/holidays")
    public ResponseEntity<List<LocalDate>> getHolidayDates(@RequestParam Long facilityNum) {
        return ResponseEntity.ok(facilityService.getHolidayDates(facilityNum));
    }

    // 특정 날짜가 휴무일인지 여부 확인
    @GetMapping("/holiday/check")
    public ResponseEntity<Boolean> isHoliday(
            @RequestParam Long facilityNum,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(facilityService.isHoliday(facilityNum, date));
    }
}
