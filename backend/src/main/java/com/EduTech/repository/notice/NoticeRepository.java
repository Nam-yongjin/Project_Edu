package com.EduTech.repository.notice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.entity.notice.Notice;

public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice>{ //대상이 되는 Entity, PK의 속성 타입
	@EntityGraph(attributePaths = {"noticeFiles"})	//파일 삭제에서 N+1문제가 계속 발생하서 넣음																//Specification 적용하기 위해 추가
	Page<Notice> findAll(Specification<Notice> spec, Pageable pageable); //Specification 조건에 맞는 공지사항을 페이징 처리해서 가져옴
	
	List<Notice> findAllByIsPinned(boolean isPinned, Sort sort); //고정여부에 따라 공지 조회, 원하는 정렬 방식 전달
	
	@Modifying
	@Transactional
    @Query("DELETE FROM Notice n WHERE n.noticeNum IN :noticeNums")
    void deleteByNoticeNumIn(@Param("noticeNums") List<Long> noticeNums); //일괄삭제(noticeNum값들의 목록)
	//수정 페이지에서 무한루프, 네트워크 에러 발생해서 넣음
	//Query 사용해서 fetch join을 명시적으로 처리
	@Query("SELECT n FROM Notice n LEFT JOIN FETCH n.noticeFiles WHERE n.noticeNum = :noticeNum")
	Optional<Notice> findByIdWithFiles(@Param("noticeNum") Long noticeNum);
	
	@Modifying
	@Query("UPDATE Notice n SET n.viewCount = n.viewCount + 1 WHERE n.noticeNum = :noticeNum")
	int increaseViewCount(@Param("noticeNum") Long noticeNum);
		
}
