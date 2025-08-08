package com.EduTech.repository.facility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.facility.FacilityReserve;
import com.EduTech.entity.facility.FacilityState;

public interface FacilityReserveRepository extends JpaRepository<FacilityReserve, Long> {

    // 내 예약 내역 (마이페이지)
    List<FacilityReserve> findByMember_MemIdOrderByReserveAtDesc(String memId);

    // 상태 필터
    List<FacilityReserve> findByState(FacilityState state);

    // 시간 겹침 여부 (상태 In 포함) 
    // newStart ~ newEnd 과 기존 (start,end) 겹침 조건: start < newEnd AND end > newStart
    boolean existsByFacility_FacRevNumAndFacDateAndStartTimeLessThanAndEndTimeGreaterThanAndStateIn(
            Long facRevNum,
            LocalDate facDate,
            LocalTime newEnd,
            LocalTime newStart,
            Collection<FacilityState> states
    );

    // 관리자 검색
    @Query("""
            SELECT r FROM FacilityReserve r
            WHERE (:state IS NULL OR r.state = :state)
              AND (:from IS NULL OR r.facDate >= :from)
              AND (:to IS NULL OR r.facDate <= :to)
            ORDER BY r.reserveAt DESC
           """)
    List<FacilityReserve> searchForAdmin(@Param("state") FacilityState state,
                                         @Param("from") LocalDate from,
                                         @Param("to") LocalDate to);

    // 상태 변경: 반드시 reserveId 기준으로!
    @Modifying(clearAutomatically = true)
    @Query("""
    	    UPDATE FacilityReserve r
    	    SET r.state = :newState
            WHERE r.reserveId = :reserveId
            AND r.state = :currentState
           """)
    int updateStateIfCurrent(@Param("reserveId") Long reserveId,
                             @Param("newState") FacilityState newState,
                             @Param("currentState") FacilityState currentState);
    
    // 취소 처리: reserveId 기준 + 관리자인지/본인인지 체크
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @org.springframework.transaction.annotation.Transactional
    @Query("""
            UPDATE FacilityReserve r
            SET r.state = 'CANCELLED'
            WHERE r.reserveId = :reserveId
              AND (:isAdmin = true OR r.member.memId = :memId)
              AND r.state IN ('WAITING', 'APPROVED')
           """)
    int cancelReservation(@Param("reserveId") Long reserveId,
                          @Param("isAdmin") boolean isAdmin,
                          @Param("memId") String memId);

    // 특정 시설+날짜의 예약들
    List<FacilityReserve> findByFacility_FacRevNumAndFacDate(Long facRevNum, LocalDate facDate);

    // 회원 탈퇴 제한 체크
    @Query("""
            SELECT COUNT(r) > 0 FROM FacilityReserve r
            WHERE r.member.memId = :memId AND r.state = 'APPROVED'
           """)
    boolean existsApprovedReservationByMemId(@Param("memId") String memId);
}
