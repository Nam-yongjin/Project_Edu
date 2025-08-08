package com.EduTech.repository.event;

import com.EduTech.entity.event.EventUse;
import com.EduTech.entity.event.RevState;
import com.EduTech.entity.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// EventUseRepository
// 행사 신청(예약) 관련 DB 작업 처리 인터페이스
public interface EventUseRepository extends JpaRepository<EventUse, Long> {

    // ================================
    // 1. 신청 여부 및 신청자 수 관련
    // ================================

    // ✅ 중복 신청 여부 확인
    boolean existsByEventInfo_EventNumAndMember_MemIdAndRevState(Long eventNum, String memId, RevState revState);

    // ✅ 신청 인원 수 count
    long countByEventInfo_EventNum(Long eventNum);

    // ✅ JPQL 버전의 count (특정 상황에서 사용)
    @Query("SELECT COUNT(p) FROM EventUse p WHERE p.eventInfo.eventNum = :eventNum")
    int countByEventInfo(@Param("eventNum") Long eventNum);


    // ================================
    // 2. 사용자 신청 내역 조회
    // ================================

    // ✅ 사용자 신청 목록 (List)
    List<EventUse> findByMember_MemId(String memId);

    // ✅ 사용자 신청 목록 (Page)
    Page<EventUse> findByMember_MemId(String memId, Pageable pageable);

    // ❌ 사용되지 않음 → 주석 처리
    // Page<EventUse> findByMember(Member member, Pageable pageable);

    // ✅ 행사별 신청 내역 전체
    List<EventUse> findByEventInfo_EventNum(Long eventNum);

    // ✅ N+1 방지용 Fetch Join
    @Query("SELECT pu FROM EventUse pu JOIN FETCH pu.member WHERE pu.eventInfo.eventNum = :eventNum")
    List<EventUse> findWithMemberByEventInfo_EventNum(@Param("eventNum") Long eventNum);


    // ================================
    // 3. 회원 탈퇴 관련
    // ================================

    // ✅ 회원의 신청 내역 일괄 삭제
    void deleteByMember_MemId(String memId);

    // ✅ 신청 승인 상태가 남아있는지 확인
    @Query("SELECT COUNT(e) > 0 FROM EventUse e WHERE e.member.memId = :memId AND e.revState = 'APPROVED'")
    boolean existsApprovedReservationByMemId(@Param("memId") String memId);
}
