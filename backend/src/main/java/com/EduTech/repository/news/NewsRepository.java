package com.EduTech.repository.news;

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

import com.EduTech.entity.news.News;

import jakarta.transaction.Transactional;

public interface NewsRepository extends JpaRepository<News, Long>, JpaSpecificationExecutor<News>{ //대상이 되는 Entity, PK의 속성 타입
	
	Page<News> findAll(Specification<News> spec, Pageable pageable); //Specification 조건에 맞는 뉴스를 페이징 처리해서 가져옴
	
	@Modifying
	@Transactional
    @Query("DELETE FROM News n WHERE n.newsNum IN :newsNums")
    void deleteByNewsNumIn(@Param("newsNums") List<Long> newsNums); //일괄삭제(newsNum값들의 목록)
	
	@Modifying
	@Query("UPDATE News n SET n.viewCount = n.viewCount + 1 WHERE n.newsNum = :newsNum")
	int increaseViewCount(@Param("newsNum") Long newsNum);

}
