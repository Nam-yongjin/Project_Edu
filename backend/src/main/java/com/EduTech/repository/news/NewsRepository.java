package com.EduTech.repository.news;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.EduTech.entity.news.News;

import jakarta.transaction.Transactional;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News>{ //대상이 되는 Entity, PK의 속성 타입
	//Specification 적용하기 위해 추가
	Page<News> findAll(Specification<News> spec, Pageable pageable); //Specification 조건에 맞는 공지사항을 페이징 처리해서 가져옴

	@Transactional
	void deleteBynewsNumIn(List<Long> ids); //일괄삭제(newsNum값들의 목록)

}
