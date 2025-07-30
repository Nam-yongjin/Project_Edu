package com.EduTech.repository.news;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.news.News;
import com.EduTech.entity.news.NewsFile;
import com.EduTech.entity.notice.NoticeFile;
//JpaRepository를 상속받아야 Bean으로 등록 가능(Test 시 @Autowired로 주입하려면 꼭 작성해야 함!!)
public interface NewsFileRepository extends JpaRepository<NewsFile, Long>{
	
	List<NewsFile> findByNews_NewsNum(Long newsNum); //언론보도 번호로 첨부파일 조회
    
    void deleteByNews_NewsNum(Long newsNum); //첨부파일 삭제

}
