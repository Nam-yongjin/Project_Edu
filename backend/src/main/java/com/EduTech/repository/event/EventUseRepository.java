package com.EduTech.repository.event;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.event.EventUse;
import com.EduTech.entity.member.Member;

public interface EventUseRepository extends JpaRepository<EventUse, Long>{

	boolean existsByEventInfo_EventNumAndMember_MemId(Long eventNum, String memId);	// 신청 여부 확인
	
	long countByEventInfo_EventNum(Long eventNum); // 신청자수 확인

	@Query("SELECT COUNT(p) FROM EventUse p WHERE p.eventInfo.eventNum = :eventNum")	// 신청자 수 카운트
	int countByEventInfo(@Param("eventNum") Long eventNum);

	List<EventUse> findByMember_MemId(String memId);	// 회원별 신청목록 조회(리스트 형태)

	Page<EventUse> findByMember_MemId(String memId, Pageable pageable);	// 회원별 신청목록 조회(페이지 형태)

	List<EventUse> findByEventInfo_EventNum(Long eventNum);	// 신청한 프로그램의 모든정보 조회

	Page<EventUse> findByMember(Member member, Pageable pageable);	// Member기준으로 예약내역 조회

	// member 함께 로딩해서 DTO 변환시 NPE(널포인터예외) 방지
	@Query("SELECT pu FROM EventUse pu JOIN FETCH pu.member WHERE pu.eventInfo.eventNum = :eventNum")
	List<EventUse> findWithMemberByEventInfo_EventNum(@Param("eventNum") Long eventNum);

	// ---------- 탈퇴 회원 프로그램 신청 내역 삭제 ----------

	// 특정 회원 프로그램 신청 내역 모두 삭제(DB에서 바로 DELETE쿼리 실행)
	void deleteByMember_MemId(String memId);

	// 특정 회원 프로그램 신청 내역을 모두 조회(필요시 사용하면 됨, 현재는 필요 없음)
	List<EventUse> findAllByMember_MemId(String memId);
	
}
