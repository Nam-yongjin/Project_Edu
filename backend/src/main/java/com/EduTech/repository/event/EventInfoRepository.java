package com.EduTech.repository.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
	
	// 지정된 날짜보다 같거나 이후인 행사를 정렬
	List<EventInfo> findByEventEndPeriodGreaterThanEqual(LocalDateTime localDateTime, Sort sort); 
	
}
