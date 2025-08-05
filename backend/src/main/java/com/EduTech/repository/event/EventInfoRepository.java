package com.EduTech.repository.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.event.EventSearchRequestDTO;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;

public interface EventInfoRepository extends JpaRepository<EventInfo, Long> {
	
	// 사용자 검색
	@Query("""
			SELECT p FROM EventInfo p
			WHERE
				(
					(:keyword IS NULL OR :keyword = '')
					OR
					(
						:searchType = 'eventName' AND p.eventName LIKE %:keyword%
				 	)
				  	OR
					(
						:searchType = 'eventInfo' AND p.eventInfo LIKE %:keyword%
					)
					OR
					(
						:searchType = 'all' AND (p.eventName LIKE %:keyword% OR p.eventInfo LIKE %:keyword%)
					)
				)
			 	AND (:state IS NULL OR p.state = :state)
				AND (:eventEndPeriod IS NULL OR p.applyEndPeriod <= :eventEndPeriod)
			""")
	Page<EventInfo> searchEvent(@Param("searchType") String searchType, @Param("keyword") String keyword,
							@Param("state") EventState state, @Param("eventStartPeriod") LocalDateTime eventStartPeriod,
							@Param("eventEndPeriod") LocalDateTime eventEndPeriod, Pageable pageable);
										// state 오류시 EventState 가 아니라 String 로 변경
	// 관리자 조건 검색
	@Query("""
				SELECT p FROM EventInfo p
				WHERE
					(
				    	(:searchType IS NULL OR :searchType = '' OR :keyword IS NULL OR :keyword = '')
				       	OR
				        (:searchType = 'eventName' AND p.eventName LIKE %:keyword%)
				   	)
					AND (:state IS NULL OR p.state = :state)
				 	AND (:eventEndPeriod IS NULL OR p.applyEndPeriod <= :eventEndPeriod)
			""")
	Page<EventInfo> searchAdminEvent(@Param("searchType") String searchType, @Param("keyword") String keyword,
								 @Param("state") EventState state, @Param("eventStartPeriod") java.time.LocalDateTime eventStartPeriod,
								 @Param("eventEndPeriod") java.time.LocalDateTime eventEndPeriod, Pageable pageable);
	
	// 관리자 복합 검색
	@Query("""
				SELECT p FROM EventInfo p
				WHERE
					(:eventName IS NULL OR p.eventName LIKE %:eventName%)
					AND (:eventInfo IS NULL OR p.eventInfo LIKE %:eventInfo%)
			""")
	Page<EventInfo> searchEvent(@Param("eventName") String eventName, @Param("eventInfo") String eventInfo, Pageable pageable);
	
	// 신청 후 신청인원 증가 코드
	@Modifying
	@Transactional // 이 메서드 단독 실행 시 트랜잭션 보장
	@Query("UPDATE EventInfo e SET e.currCapacity = e.currCapacity + 1 WHERE e.eventNum = :eventNum")
	void increaseCurrCapacity(@Param("eventNum") Long eventNum);
	
	// 상태를 OPEN으로 변경
	@Modifying
	@Transactional
	@Query("UPDATE EventInfo e SET e.state = 'OPEN' WHERE CURRENT_TIMESTAMP BETWEEN e.applyStartPeriod AND e.applyEndPeriod AND e.state <> 'CANCEL'")
	int updateStateToOpen();

	// 상태를 BEFORE로 변경
	@Modifying
	@Transactional
	@Query("UPDATE EventInfo e SET e.state = 'BEFORE' WHERE CURRENT_TIMESTAMP < e.applyStartPeriod AND e.state <> 'CANCEL'")
	int updateStateToBefore();

	// 상태를 CLOSED로 변경
	@Modifying
	@Transactional
	@Query("UPDATE EventInfo e SET e.state = 'CLOSED' WHERE CURRENT_TIMESTAMP > e.applyEndPeriod AND e.state <> 'CANCEL'")
	int updateStateToClosed();
	
	// 검색 관련 코드
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
	
	// 지정된 날짜보다 같거나 이후인 행사를 정렬
	List<EventInfo> findByEventEndPeriodGreaterThanEqual(LocalDateTime localDateTime, Sort sort); 
	
}
