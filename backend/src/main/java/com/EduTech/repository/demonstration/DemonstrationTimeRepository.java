package com.EduTech.repository.demonstration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationTimeReqDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeResDTO;
import com.EduTech.entity.demonstration.DemonstrationTime;

public interface DemonstrationTimeRepository extends JpaRepository<DemonstrationTime, Long> { // 실증 시간 레포지토리 
	
	// 클라이언트로부터 시작 날짜와 끝 날짜, 실증 제품 번호를 가져와
	// 해당 날짜가 예약 되있는지 아닌지 상태값 받는 쿼리
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationTimeResDTO(dt.demDate,dt.demonstration.demNum) FROM DemonstrationTime dt WHERE dt.demDate BETWEEN :startDate AND :endDate AND dt.demonstration.demNum = :demNum")
	List<DemonstrationTimeResDTO> findReservedDates(
	    @Param("startDate") LocalDate startDate,
	    @Param("endDate") LocalDate endDate,
	    @Param("demNum") Long demNum
	);
	
	// 기존 날짜 정보를 가져와 삭제하는 쿼리문
	@Modifying
	@Transactional
	@Query("DELETE FROM DemonstrationTime WHERE demDate BETWEEN :startDate AND :endDate")
	void deleteDemTimes(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
	
	// startDate와 endDate사이의 값들을 가져와 해당된 값을 전부 삭제하는 쿼리문
	@Modifying
	@Transactional
	@Query("DELETE FROM DemonstrationTime WHERE demDate IN :demDate")
	void deleteDemTimeList(@Param("demDate") List<LocalDate> demDate);
	
	@Modifying
	@Transactional
	@Query("DELETE FROM DemonstrationTime WHERE demDate IN :demDate AND demonstration.demNum=:demNum")
	void deleteTimeDemNum(@Param("demDate") List<LocalDate> demDate,@Param("demNum") Long demNum);

}
