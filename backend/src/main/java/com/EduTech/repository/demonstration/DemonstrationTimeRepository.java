package com.EduTech.repository.demonstration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationTimeResDTO;
import com.EduTech.entity.demonstration.DemonstrationTime;

public interface DemonstrationTimeRepository extends JpaRepository<DemonstrationTime, Long> { // 실증 시간 레포지토리 (상세시간)
	// 이미지 삭제와 실증 예약 시간 삭제같은 경우는 고아 처리를해 실증을 삭제하면 전부 삭제되게 구현해 놓았기 때문에 하지않았음.
	
	// 클라이언트로부터 시작 날짜와 끝 날짜, 실증 제품 번호를 가져와
	// 해당 날짜가 예약 되있는지 아닌지 상태값 받는 쿼리
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationTimeResDTO(dt.demDate,dt.state,dt.demonstration.demNum) FROM DemonstrationTime dt WHERE dt.demDate BETWEEN :startDate AND :endDate AND dt.demonstration.demNum = :demNum")
	List<DemonstrationTimeResDTO> findReservedDates(
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate,
	    @Param("demNum") Long demNum
	);

	// 수정된 데이터가 기존 데이터 보다 더 짧은 날짜일 경우,
	// 그 사이의 날짜에 대하여 삭제하는 쿼리문
	@Modifying
	@Transactional
	@Query("DELETE FROM DemonstrationTime WHERE demDate IN:demDates")
	void deleteDemTimes(@Param("demDates") List<LocalDate> demDates);
}
