package com.EduTech.service.news;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;

public interface NewsService {
	
		//공지사항 등록
		void createNews(NewsCreateRegisterDTO dto, List<MultipartFile> file);
		
		//공지사항 수정
		void updateNews(NewsUpdateRegisterDTO dto, List<MultipartFile> file, Long newsNum);
		
		//공지사항 삭제(단일)
		void deleteNews(Long newsNum);
		
		//공지사항 삭제(일괄)
		void deleteNews(List<Long> newsNums);
		
		//공지사항 상세 조회
		NewsDetailDTO getNewsDetail(Long newsNum);
		
		//공지사항 전체 조회
		Page<NewsListDTO> getNewsList(NewsSearchDTO dto, Pageable pageable);
		
		//상세 공지 조회 시 조회수 증가
		void increaseView(Long newsNum);

}