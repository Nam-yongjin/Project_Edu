package com.EduTech.repository.notice;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.EduTech.entity.notice.Notice;

import jakarta.transaction.Transactional;

public interface NoticeRepository extends JpaRepository<Notice, Long>, JpaSpecificationExecutor<Notice>{ //대상이 되는 Entity, PK의 속성 타입
																		//Specification 적용하기 위해 추가
	Page<Notice> findAll(Specification<Notice> spec, Pageable pageable); //Specification 조건에 맞는 공지사항을 페이징 처리해서 가져옴
	
	List<Notice> findAllByIsPinned(boolean isPinned, Sort sort); //고정여부에 따라 공지 조회, 원하는 정렬 방식 전달
	
	@Transactional
	void deleteBynoticeNumIn(List<Long> ids); //일괄삭제(noticeNum값들의 목록)
		
}//쿼리문으로 바꾸기??

