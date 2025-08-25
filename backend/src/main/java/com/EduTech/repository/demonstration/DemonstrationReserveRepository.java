package com.EduTech.repository.demonstration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.admin.ExpiredUserDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeReqDTO;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;

public interface DemonstrationReserveRepository
		extends JpaRepository<DemonstrationReserve, Long>, JpaSpecificationExecutor<DemonstrationReserve> { 
																											
	// 아이디와 상품번호를 받아와 현재 상태가 취소가 아닌 res테이블을 가져오는 쿼리문
	@Query("SELECT dr FROM DemonstrationReserve dr WHERE dr.demRevNum IN :demRevNum AND dr.state=:state")
	List<DemonstrationReserve> findDemRevNum(@Param("demRevNum") List<Long> demRevNum,
			@Param("state") DemonstrationState state);

	// 장비 신청 상세페이지에서 날짜 선택후 예약 신청하기 누르면 예약이 변경되는 쿼리문
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationReserve dr SET startDate=:startDate, endDate=:endDate WHERE dr.demonstration.demNum = :demNum AND dr.member.memId=:memId")
	int updateDemResChangeDateAll(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("demNum") Long demNum, @Param("memId") String memId);

	@Modifying // demonstration reserve 테이블 상태값 변경 쿼리문 (아이디와 실증 번호를 받음)
	@Transactional
	@Query("UPDATE DemonstrationReserve SET state=:state WHERE demRevNum IN :demRevNum")
	int updateDemResChangeState(@Param("state") DemonstrationState state,
			@Param("demRevNum") List<Long> demRevNum);

	 // 물품 대여 조회 페이지에서 연기 신청, 반납 조기 신청 버튼 클릭 시, endDate를 변경하는 쿼리문
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationReserve SET endDate=:endDate WHERE demRevNum IN :demRevNum AND state=:state")
	int updateDemResEndDate(@Param("demRevNum") List<Long> demRevNum, @Param("endDate") LocalDate endDate,
			@Param("state") DemonstrationState state);

	@Modifying // demonstration reserve 테이블 상태값 변경 쿼리문 (실증 신청 번호를 받음)
	@Transactional
	@Query("UPDATE DemonstrationReserve SET state=:state WHERE demRevNum IN :demRevNum")
	int updateDemResChangeStateRev(@Param("state") DemonstrationState state, @Param("demRevNum") List<Long> demRevNum);

	// 나중에 회원 탈퇴할때, 실증 신청 중인 상태이면 회원 탈퇴 못하도록 구현하기 위한 쿼리문
	@Query("SELECT COUNT(d) > 0 FROM DemonstrationReserve d WHERE d.member.memId = :memId AND d.state = 'ACCEPT'")
	boolean existsAcceptedReserveByMemId(@Param("memId") String memId);

	// 물품 예약 신청할때 해당 회원이 동일한 상품에 예약을 할 경우, 막기 위해 만든 쿼리문
	@Query("SELECT COUNT(r) > 0 FROM DemonstrationReserve r WHERE r.member.memId = :memId AND r.demonstration.demNum = :demNum AND r.state IN:state")
	Optional<Boolean> checkRes(@Param("demNum") Long demNum, @Param("memId") String memId,
			@Param("state") List<DemonstrationState> state);

	// RES테이블에서 아이디와 상품번호를 받아와 물품 대여한 갯수를 가져오는 쿼리문
	@Query("SELECT (r.bItemNum+d.itemNum) FROM DemonstrationReserve r,Demonstration d WHERE d.demNum=r.demonstration.demNum AND r.demonstration.demNum =:demNum AND r.state=:state AND r.member.memId=:memId")
	Long getBItemNum(@Param("demNum") Long demNum, 
			@Param("state") DemonstrationState state,@Param("memId")String memId);

	// 스케줄러를 이용해 state가 cancel인 값 삭제
	@Modifying
	@Transactional
	@Query("DELETE FROM DemonstrationReserve WHERE state=:state")
	void deleteResCancel(@Param("state") DemonstrationState state);

	// RES테이블에서 아이디와 상품번호를 받아와 예약 시작날짜와 끝날짜를 받아오는 쿼리문
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationTimeReqDTO(r.startDate,r.endDate) FROM DemonstrationReserve r WHERE r.demonstration.demNum=:demNum AND r.member.memId = :memId AND r.demonstration.demNum IN :demNum AND r.state=:stateWait")
	DemonstrationTimeReqDTO getResDate(@Param("demNum") Long demNum, @Param("memId") String memId,@Param("stateWait") DemonstrationState wait);

	// res테이블에서 아이디와 상품 번호를 받아와 실증 신청 번호를 받아오는 쿼리문
	@Query("SELECT r FROM DemonstrationReserve r WHERE r.demonstration.demNum=:demNum AND r.member.memId = :memId AND r.demonstration.demNum IN :demNum AND r.state!=:state")
	DemonstrationReserve getRev(@Param("demNum") Long demNum, @Param("memId") String memId,
			@Param("state") DemonstrationState state);

	// res테이블에서 상품번호를 받아와 해당 상품을 신청한 회원 아이디를 받아오는 쿼리문
	@Query("SELECT r.member.memId FROM DemonstrationReserve r WHERE r.demonstration.demNum IN :demNum AND r.state!=:state")
	List<String> getResMemId(@Param("demNum") List<Long> demNum, @Param("state") DemonstrationState state);

	// 여러 memId, 여러 demNum에 대해 취소 상태가 아닌 예약 목록 조회
	@Query("SELECT dr FROM DemonstrationReserve dr WHERE dr.demRevNum IN :demRevNums AND dr.state =:state")
	List<DemonstrationReserve> findDemRevNums(@Param("demRevNums") List<Long> demRevNums, @Param("state") DemonstrationState state);

	// 여러 memId, 한 demNum 기준으로 bItemNum + itemNum 합산 (단일 Long 반환)
	@Query("SELECT SUM(r.bItemNum + d.itemNum) FROM DemonstrationReserve r JOIN Demonstration d ON d.demNum = r.demonstration.demNum WHERE r.demonstration.demNum = :demNum AND r.member.memId IN :memIds AND r.state = :state")
	Long getBItemNumBatch(@Param("demNum") Long demNum, @Param("memIds") List<String> memIds,
			@Param("state") DemonstrationState state);

	// 여러 memId, 여러 demNum 상태 일괄 변경
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationReserve dr SET dr.state = :state WHERE dr.member.memId IN :memIds AND dr.demonstration.demNum IN :demNums")
	int updateDemResChangeStateBatch(@Param("state") DemonstrationState state, @Param("memIds") List<String> memIds,
			@Param("demNums") List<Long> demNums);

	// 여러 상품번호를 받고 그 상품을 대여한 회원 목록 받아옴
	List<DemonstrationReserve> findByDemonstration_DemNumIn(List<Long> demNums);

	// 현재 날짜와 만료 날짜를 비고해서 만료날짜가 작거나 같을경우 상태를 만료로 업데이트
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationReserve r SET r.state = :expired WHERE r.endDate <= :today AND r.state=:accept")
	int changeResExpiredState(@Param("today") LocalDate today, @Param("expired") DemonstrationState expired,@Param("accept") DemonstrationState accept);
	
	// 반납 기한을 넘긴 회원 이메일 및 날짜를 가져오는 쿼리문
	@Query("SELECT new com.EduTech.dto.admin.ExpiredUserDTO(m.email,dr.endDate) " +
	       "FROM DemonstrationReserve dr JOIN dr.member m " +
	       "WHERE dr.state = :state")
	List<ExpiredUserDTO> findExpiredInfo(@Param("state") DemonstrationState state);

}
