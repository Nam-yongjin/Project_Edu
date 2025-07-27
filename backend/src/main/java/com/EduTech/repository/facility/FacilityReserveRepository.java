package com.EduTech.repository.facility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.facility.FacilityReserve;
import com.EduTech.entity.facility.FacilityState;

public interface FacilityReserveRepository extends JpaRepository<FacilityReserve, Long> {

    // 내 예약 내역 조회 (마이페이지)
    List<FacilityReserve> findByMemIdOrderByReserveAtDesc(String memId);

    // 특정 상태(WAITING, APPROVED 등) 예약들만 조회 (관리자용 필터)
    List<FacilityReserve> findByState(FacilityState state);

    // 예약 시간 겹침 여부 확인 (예약 가능 여부 판단용)
    boolean existsByFacility_FacilityNumAndFacDateAndStartTimeLessThanAndEndTimeGreaterThanAndStateIn(
        Long facilityNum,
        LocalDate facDate,
        LocalTime endTime,
        LocalTime startTime,
        List<FacilityState> states
    );

    // 관리자: 조건 기반 예약 검색 (상태 + 날짜범위 필터링)
    @Query("""
        SELECT r FROM FacilityReserve r
        WHERE (:state IS NULL OR r.state = :state)
          AND (:from IS NULL OR r.facDate >= :from)
          AND (:to IS NULL OR r.facDate <= :to)
        ORDER BY r.reserveAt DESC
    """)
    List<FacilityReserve> searchForAdmin(
        @Param("state") FacilityState state,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );

    // 관리자: 승인/거절 상태 변경 (중복 방지 위해 현재 상태 조건 포함)
    @Modifying
    @Query("""
        UPDATE FacilityReserve r
        SET r.state = :newState
        WHERE r.facRevNum = :facRevNum AND r.state = :currentState
    """)
    int updateStateIfCurrent(
        @Param("facRevNum") Long facRevNum,
        @Param("newState") FacilityState newState,
        @Param("currentState") FacilityState currentState
    );
    
    // 예약 상태를 'CANCELLED'로 변경하는 쿼리
    @Modifying
    @Query("""
        UPDATE FacilityReserve r
        SET r.state = 'CANCELLED'
        WHERE r.facRevNum = :facRevNum
          AND (:isAdmin = true OR r.memId = :memId) 
          AND r.state IN ('WAITING', 'APPROVED')
    """)
    int cancelReservation(
        @Param("facRevNum") Long facRevNum,
        @Param("isAdmin") boolean isAdmin,
        @Param("memId") String memId
    );

    // 특정 시설+날짜에 대한 예약 전체 조회 (예약 현황 등)
    List<FacilityReserve> findByFacility_FacilityNumAndFacDate(Long facilityNum, LocalDate facDate);
}
