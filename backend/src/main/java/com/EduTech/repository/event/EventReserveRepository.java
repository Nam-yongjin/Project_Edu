package com.EduTech.repository.event;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.event.EventReserve;
import com.EduTech.entity.member.Member;

public interface EventReserveRepository extends JpaRepository<EventReserve, Long>{
	
	boolean existsByEvent_eventNumAndMember_memId(Long eventNum, String mid);	// 중복신청 여부 확인
	
	@Query("SELECT COUNT(p) FROM EventReserve p WHERE p.event.eventNum = :eventNum")	// 신청자 수 카운트
	int countByEvent(@Param("eventNum") Long progNo);
	
	List<EventReserve> findByMember_Mid(String mid);	// 회원별 신청목록 조회(리스트 형태)
	
	Page<EventReserve> findByMember_Mid(String mid, Pageable pageable);	// 회원별 신청목록 조회(페이지 형태)
	
	List<EventReserve> findByEvent_eventNum(Long eventNum);	// 신청한 프로그램의 모든정보 조회
	
	Page<EventReserve> findByMember(Member member, Pageable pageable);	// Member기준으로 예약내역 조회
	
	// member 함께 로딩해서 DTO 변환시 NPE(널포인터예외) 방지
	@Query("SELECT pu FROM EventReserve pu JOIN FETCH pu.member WHERE pu.event.eventNum = :eventNum")
	List<EventReserve> findWithMemberByEvent_eventNum(@Param("eventNum") Long eventNum);
	
	// ---------- 탈퇴 회원 행사 신청 내역 삭제 ----------
	
	void deleteByMember_Mid(String mid); // 특정 회원 행사 신청 내역 모두 삭제 (DB상에서 바로 DELETE쿼리 실행)
	
	List<EventReserve> findAllByMember_Mid(String mid);	// 특정 회원 행사 신청 내역을 모두 조회(필요시 사용하면 됨, 현재는 필요 없음)
	
}
