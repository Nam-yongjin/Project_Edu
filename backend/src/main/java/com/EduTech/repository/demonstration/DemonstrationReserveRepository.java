package com.EduTech.repository.demonstration;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;

public interface DemonstrationReserveRepository
		extends JpaRepository<DemonstrationReserve, Long>, JpaSpecificationExecutor<DemonstrationReserve> { // 실증 교사 신청
																											// 관련 레포지토리

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

	// (관리자 실증교사 신청 조회 페이지) 받아올 dto 추가 필요함. 검색 추가(조인 추가해서)
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationListReserveDTO(res.demRevNum,res.applyAt, res.state, res.member.memId, t.schoolName) FROM DemonstrationReserve res, Teacher t WHERE res.member.memId=t.memId AND t.schoolName LIKE %:search%")
	Page<DemonstrationListReserveDTO> selectPageDemResSearch(Pageable pageable, @Param("search") String search);

	// 백쪽에서만 사용할거면 dto대신 엔티티로 받아도 문제없다.
	@Query("SELECT dr FROM DemonstrationReserve dr WHERE dr.member.memId = :memId AND dr.demonstration.demNum = :demNum")
	DemonstrationReserve findDemRevNum(@Param("memId") String memId, @Param("demNum") Long demNum);

	// 장비 신청 상세페이지에서 날짜 선택후 예약 신청하기 누르면 예약이 변경되는 쿼리문 (실증 예약 가능 시간도 업데이트 해줘야함) -
	// demonstrationReserve 테이블의 값을 수정하니 해당 리포지토리에 작성함.
	@Modifying
	@Transactional
	@Query("UPDATE DemonstrationReserve dr SET startDate=:startDate, endDate=:endDate WHERE dr.demonstration.demNum = :demNum AND dr.member.memId=:memId")
	int updateDemResChangeDateAll(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("demNum") Long demNum, @Param("memId") String memId);

	@Modifying // 물품 대여 조회 페이지에서 연기 신청, 반납 조기 신청 버튼 클릭 시, endDate를 변경하는 쿼리문
	@Query("UPDATE DemonstrationReserve SET endDate=:endDate, startDate=:startDate WHERE demRevNum=:demRevNum AND member.memId=:memId")
	@Transactional
	int updateDemResChangeDate(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate,
			@Param("demRevNum") Long demRevNum, @Param("memId") String memId);

	@Modifying // 실증 교사 신청 목록 페이지에서 승인 / 거부 버튼 클릭 시, state를 변경하는 쿼리문
	@Transactional
	@Query("UPDATE DemonstrationReserve dr SET state=:state WHERE member.memId=:memId AND demRevNum=:demRevNum")
	int updateDemResChangeState(@Param("state") DemonstrationState state, @Param("memId") String memId,
			@Param("demRevNum") Long demRevNum);

	// 나중에 회원 탈퇴할때, 실증 신청 중인 상태이면 회원 탈퇴 못하도록 구현하기 위한 쿼리문
	@Query("SELECT COUNT(d) > 0 FROM DemonstrationReserve d WHERE d.member.memId = :memId AND d.state = 'ACCEPT'")
	boolean existsAcceptedReserveByMemId(@Param("memId") String memId);

	// 물품 예약 신청할때 해당 회원이 동일한 상품에 예약을 할 경우, 막기 위해 만든 쿼리문
	@Query("SELECT COUNT(r) > 0 FROM DemonstrationReserve r WHERE r.member.memId = :memId AND r.demonstration.demNum = :demNum")
	Optional<Boolean> checkRes(@Param("demNum") Long demNum, @Param("memId") String memId);

	@Override
	@EntityGraph(attributePaths = { "demonstration", "demonstration.demonstrationRegistration",
			"demonstration.demonstrationRegistration.member",
			"demonstration.demonstrationRegistration.member.company" })
	Page<DemonstrationReserve> findAll(Specification<DemonstrationReserve> spec, Pageable pageable);

}
