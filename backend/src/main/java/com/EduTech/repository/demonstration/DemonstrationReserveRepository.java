package com.EduTech.repository.demonstration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;

public interface DemonstrationReserveRepository extends JpaRepository<DemonstrationReserve, Long> { // 실증 교사 신청 관련 레포지토리

	@Modifying
	@Transactional // 트랜잭션 처리 (실행중 오류가 발생했을때, rollback 처리를 하여 db 무결성을 해치지 않도록 함.)
	@Query("DELETE FROM DemonstrationReserve WHERE demRevNum=:demRevNum")
	void deleteOneDemRes(@Param("demRevNum") long demRevNum); // 회원이 신청한 실증 신청 삭제

	@Modifying
	@Transactional
	@Query("DELETE FROM DemonstrationReserve WHERE demRevNum IN :demRevNum")
	void deleteMembersDemRes(@Param("demRevNum") List<Long> demRevNum); // 회원이 신청한 실증 신청 삭제(다수)

	// (관리자 실증교사 신청 조회 페이지) 받아올 dto 추가 필요함. (조인 추가해서)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationListReserveDTO(res.demRevNum,res.applyAt, res.state, res.member.memId, t.schoolName) FROM DemonstrationReserve res, Teacher t WHERE res.member.memId=t.memId")
	Page<DemonstrationListReserveDTO> selectPageDemRes(Pageable pageable);

	@Modifying // 장비 신청 상세페이지에서 날짜 선택후 예약 신청하기 누르면 예약이 변경되는 쿼리문 (실증 예약 가능 시간도 업데이트 해줘야함) -
				// demonstrationReserve 테이블의 값을 수정하니 해당 리포지토리에 작성함.
	@Transactional
	@Query("UPDATE DemonstrationReserve dr SET startDate=:startDate, endDate=:endDate WHERE dr.demonstration.demNum = :demNum AND dr.member.memId=:memId")
	int updateDemResChangeDateAll(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("demNum") Long demNum, @Param("memId") String memId);

	@Modifying // 물품 대여 조회 페이지에서 연기 신청, 반납 조기 신청 버튼 클릭 시, endDate를 변경하는 쿼리문
	@Query("UPDATE DemonstrationReserve dr SET endDate=:changeDate WHERE dr.demonstration.demNum = :demNum AND dr.member.memId=:memId")
	@Transactional
	int updateDemResChangeDate(@Param("changeDate") LocalDate changeDate, @Param("demNum") Long demNum,
			@Param("memId") String memId);

	@Modifying // 실증 교사 신청 목록 페이지에서 승인 / 거부 버튼 클릭 시, state를 변경하는 쿼리문
	@Transactional
	@Query("UPDATE DemonstrationReserve dr SET state=:state WHERE member.memId=:memId")
	int updateDemResChangeState(@Param("state") DemonstrationState state, @Param("memId") String memId);

}
