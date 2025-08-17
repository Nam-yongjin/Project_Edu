package com.EduTech.repository.demonstration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationDetailDTO;
import com.EduTech.entity.demonstration.DemonstrationRequest;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.RequestType;

public interface DemonstrationRequestRepository extends JpaRepository<DemonstrationRequest, Long> {
	// 아이디들을 가져와서 기업 목록들을 가져오는 쿼리문
	@Query("SELECT r FROM DemonstrationRequest r WHERE r.reserve.demRevNum IN :demRevNums")
	List<DemonstrationRequest> findStateByDemRevNumIn(@Param("demRevNums") List<Long> demRevNums);

	// 반납 / 대여 연기 요청에서 상태값을 업데이트하는 쿼리문
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationRequest SET state=:state WHERE reserve.demRevNum =:demRevNum AND type=:type AND state=:updateState")
	int updateDemResChangeStateReq(@Param("state") DemonstrationState state, @Param("demRevNum") Long demRevNum,
			@Param("type") RequestType type, @Param("updateState") DemonstrationState updateState);

	// 실증 상품들을 페이지 별로 가져오는 쿼리문 (실증 장비 신청 상세 페이지)
	@Query("SELECT q FROM DemonstrationRequest q WHERE reserve.demRevNum=:demRevNum AND q.state=:state")
	DemonstrationRequest selectRequest(@Param("demRevNum") Long demRevNum, @Param("state") DemonstrationState state);

	// 스케줄러에서 state가 accpet,reject일때 삭제시키는 쿼리문
	@Modifying
	@Transactional
	@Query("DELETE FROM DemonstrationRequest WHERE state=:state")
	void deleteReq(@Param("state") DemonstrationState state);
}
