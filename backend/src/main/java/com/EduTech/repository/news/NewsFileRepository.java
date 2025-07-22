package com.EduTech.repository.news;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.news.News;
import com.EduTech.entity.news.NewsFile;
//JpaRepository를 상속받아야 Bean으로 등록 가능(Test 시 @Autowired로 주입하려면 꼭 작성해야 함!!)
public interface NewsFileRepository extends JpaRepository<NewsFile, Long>{
	
	List<NewsFile> findByNews(News news); //상세 페이지에서 첨부파일 보여줄 때
	
	List<NewsFile> findByNews_NewsNum(Long newsNum); //공지사항번호로 첨부파일 조회

}
