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
	@EntityGraph(attributePaths = {"newsFiles"})	//파일 삭제에서 N+1문제가 계속 발생해서 넣음
	Page<News> findAll(Specification<News> spec, Pageable pageable); //Specification 조건에 맞는 뉴스를 페이징 처리해서 가져옴
	
	@Modifying
	@Transactional
    @Query("DELETE FROM News n WHERE n.newsNum IN :newsNums")
    void deleteByNewsNumIn(@Param("newsNums") List<Long> newsNums); //일괄삭제(newsNum값들의 목록)
	
	//수정 페이지에서 무한루프, 네트워크 에러 발생해서 넣음
	//Query 사용해서 fetch join을 명시적으로 처리
	//news 한 건을 조회해서 news랑 newFiles라는 연관관계를 즉시 조인해서 가져옴 --> 한 번의 쿼리로 news += newsFile을 한 번에 가져오는 것
	@Query("SELECT n FROM News n LEFT JOIN FETCH n.newsFiles WHERE n.newsNum = :newsNum")
	Optional<News> findByIdWithFiles(@Param("newsNum") Long newsNum);

}
