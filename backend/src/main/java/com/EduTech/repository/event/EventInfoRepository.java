package com.EduTech.repository.event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.event.Event;

public interface EventInfoRepository extends JpaRepository<Event, Long> {
	
	// 사용자 검색
	@Query("""
			SELECT p FROM Event p
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
			 	AND (:state IS NULL OR :state = '' OR p.state = :state)
				AND (:progressEndPeriod IS NULL OR p.applyEndPeriod <= :progressEndPeriod)
			""")
	Page<Event> searchEvent(@Param("searchType") String searchType, @Param("keyword") String keyword,
							@Param("state") String state, @Param("progressStartPeriod") LocalDateTime progressStartPeriod,
							@Param("progressEndPeriod") LocalDateTime progressEndPeriod, Pageable pageable);

	// 관리자 조건 검색
	@Query("""
				SELECT p FROM Event p
				WHERE
					(
				    	(:searchType IS NULL OR :searchType = '' OR :keyword IS NULL OR :keyword = '')
				       	OR
				        (:searchType = 'eventName' AND p.eventName LIKE %:keyword%)
				   	)
					AND (:state IS NULL OR :state = '' OR p.state = :state)
				 	AND (:progressEndPeriod IS NULL OR p.applyEndPeriod <= :progressEndPeriod)
			""")
	Page<Event> searchAdminEvent(@Param("searchType") String searchType, @Param("keyword") String keyword,
								 @Param("state") String state, @Param("progressStartPeriod") java.time.LocalDateTime progressStartPeriod,
								 @Param("progressEndPeriod") java.time.LocalDateTime progressEndPeriod, Pageable pageable);
	
	// 관리자 복합 검색
	@Query("""
				SELECT p FROM Event p
				WHERE
					(:eventName IS NULL OR p.eventName LIKE %:eventName%)
					AND (:eventInfo IS NULL OR p.eventInfo LIKE %:eventInfo%)
			""")
	Page<Event> searchProgram(@Param("eventName") String eventName, @Param("eventInfo") String eventInfo, Pageable pageable);
	
	// 지정된 날짜보다 같거나 이후인 행사를 정렬
	List<Event> findByprogressEndPeriodGreaterThanEqual(LocalDate progressEndPeriod, Sort sort); 
	
}
