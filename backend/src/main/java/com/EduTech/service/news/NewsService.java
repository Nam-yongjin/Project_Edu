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
import com.EduTech.dto.notice.NoticeCreateRegisterDTO;
import com.EduTech.dto.notice.NoticeDetailDTO;
import com.EduTech.dto.notice.NoticeListDTO;
import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.dto.notice.NoticeUpdateRegisterDTO;

public interface NewsService {
	
	//언론보도 등록
	Long createNews(NewsCreateRegisterDTO requestDto);
		
	//언론보도 수정
	void updateNews(Long newsNum, NewsUpdateRegisterDTO requestDto);
		
	//언론보도 삭제(단일)
	void deleteNews(Long newsNum);
		
	//언론보도 삭제(일괄)
	void deleteNewsByIds(List<Long> newsNums);
		
	//언론보도 상세 조회
	NewsDetailDTO getNewsDetail(Long newsNum);
		
	//언론보도 전체 조회
	Page<NewsListDTO> getNewsList(NewsSearchDTO searchDto);
		
	//상세 뉴스 조회 시 조회수 증가
	void increaseViewCount(Long newsNum);

}