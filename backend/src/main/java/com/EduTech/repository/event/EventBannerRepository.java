package com.EduTech.repository.event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.event.EventFile;

public interface EventBannerRepository extends JpaRepository<EventFile, Long> {
	
	boolean existsByEvent_eventNum(Long eventNum);	//eventNum의 해당하는 행사 중복 등록 방지

	Optional<EventFile> findByEvent_eventNum(Long eventNum);	//eventNum을 통해 EventFile 정보 가져옴

	// 오늘을 기준으로 진행중이거나 아직 종료되지 않은 행사 표시
	@Query("""
			    SELECT pb FROM EventFile pb
			    WHERE pb.event.progressEndPeriod >= :today
			""")
	List<EventFile> findActiveEventBanners(@Param("today") LocalDate today);

	// MAX 3개로 제한
	@Query("""
			    SELECT COUNT(pb) FROM EventFile pb
			    WHERE pb.event.progressEndPeriod >= :today
			""")
	long countValidBanners(@Param("today") LocalDate today);
	
	
}
