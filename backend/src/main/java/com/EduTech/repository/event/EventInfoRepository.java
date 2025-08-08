package com.EduTech.repository.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.event.EventSearchRequestDTO;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;

public interface EventInfoRepository extends JpaRepository<EventInfo, Long> {

    // ================================
    // 1. 사용자 검색 (프론트 검색용)
    // ================================
	
    @Query("""
        SELECT e FROM EventInfo e
        WHERE 
            (:#{#dto.keyword} IS NULL OR :#{#dto.keyword} = ''
            OR (:#{#dto.searchType} = 'eventName' AND e.eventName LIKE %:#{#dto.keyword}%)
            OR (:#{#dto.searchType} = 'eventInfo' AND e.eventInfo LIKE %:#{#dto.keyword}%)
            OR (:#{#dto.searchType} = 'all' AND (e.eventName LIKE %:#{#dto.keyword}% OR e.eventInfo LIKE %:#{#dto.keyword}%))
            )
            AND (:#{#dto.state} IS NULL OR e.state = :#{#dto.state})
            AND (:#{#dto.category} IS NULL OR e.category = :#{#dto.category})
        """)
    Page<EventInfo> searchEvents(@Param("dto") EventSearchRequestDTO dto, Pageable pageable);

    // ================================
    // 2. 상태 갱신 관련 (스케줄러용 등)
    // ================================

    // ❌ 사용되지 않음 → 주석 처리
    // @Modifying
    // @Transactional
    // @Query("UPDATE EventInfo e SET e.currCapacity = e.currCapacity + 1 WHERE e.eventNum = :eventNum")
    // void increaseCurrCapacity(@Param("eventNum") Long eventNum);

     @Modifying
     @Transactional
     @Query("UPDATE EventInfo e SET e.state = 'OPEN' WHERE CURRENT_TIMESTAMP BETWEEN e.applyStartPeriod AND e.applyEndPeriod AND e.state <> 'CANCEL'")
     int updateStateToOpen();

     @Modifying
     @Transactional
     @Query("UPDATE EventInfo e SET e.state = 'BEFORE' WHERE CURRENT_TIMESTAMP < e.applyStartPeriod AND e.state <> 'CANCEL'")
     int updateStateToBefore();

     @Modifying
     @Transactional
     @Query("UPDATE EventInfo e SET e.state = 'CLOSED' WHERE CURRENT_TIMESTAMP > e.applyEndPeriod AND e.state <> 'CANCEL'")
     int updateStateToClosed();


    // ================================
    // 3. 리스트 정렬 및 범위 검색
    // ================================

    List<EventInfo> findByEventStartPeriodBetweenAndState(LocalDateTime start, LocalDateTime end, EventState state);
}
