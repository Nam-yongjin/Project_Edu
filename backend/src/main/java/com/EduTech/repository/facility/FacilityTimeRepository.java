package com.EduTech.repository.facility;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.facility.FacilityTime;

public interface FacilityTimeRepository extends JpaRepository<FacilityTime, Long> {

    // 특정 시설 + 날짜의 예약 가능 시간대 리스트.
    List<FacilityTime> findByFacility_FacilityNumAndFacDate(Long facilityNum, LocalDate facDate);

    // 한 달 단위로 예약 가능 시간대 리스트 조회 (달력용)
    @Query("""
        SELECT ft FROM FacilityTime ft 
        WHERE ft.facility.facilityNum = :facilityNum 
          AND ft.facDate BETWEEN :start AND :end 
        ORDER BY ft.facDate, ft.startTime
    """)
    List<FacilityTime> findMonthlyTimes(
        @Param("facilityNum") Long facilityNum,
        @Param("start") LocalDate start,
        @Param("end") LocalDate end
    );
}
