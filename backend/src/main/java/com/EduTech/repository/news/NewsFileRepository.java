package com.EduTech.repository.news;

import java.util.List;

import com.EduTech.entity.news.News;
import com.EduTech.entity.news.NewsFile;

public interface NewsFileRepository {
	
	List<NewsFile> findByNews(News news); //상세 페이지에서 첨부파일 보여줄 때
	
	List<NewsFile> findByNewsNum(Long newsNum); //공지사항번호로 첨부파일 조회

}
