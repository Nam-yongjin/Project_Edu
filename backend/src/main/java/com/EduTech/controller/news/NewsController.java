package com.EduTech.controller.news;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;
import com.EduTech.service.news.NewsService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;

    // 일반 회원

    // 언론보도 전체 조회(검색, 페이징)
    @GetMapping("/NewsList")
    public ResponseEntity<Page<NewsListDTO>> getNewsList(@ModelAttribute NewsSearchDTO searchDTO) {
    	System.out.println("언론보도 전체 조회!!");
        return ResponseEntity.ok(newsService.getNewsList(searchDTO));
    }

    // 언론보도 상세 조회(조회수 증가)
    @GetMapping("/NewsDetail/{newsNum}")
    public ResponseEntity<NewsDetailDTO> getNewsDetail(@PathVariable("newsNum") Long newsNum) { //@PathVariable에 ("newsNum") 꼭 명시해주기!
    	System.out.println("언론보도 상세 조회!!");
        newsService.increaseViewCount(newsNum);
        return ResponseEntity.ok(newsService.getNewsDetail(newsNum));
    }

    // 관리자 전용

    // 언론보도 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/AddNews")
    public ResponseEntity<String> createNews(
            @RequestBody NewsCreateRegisterDTO dto) { //url만 보낼 거라 RequestBody로 수정
    	System.out.println("언론보도 등록 조회!!");
  
        Long newsNum = newsService.createNews(dto);
        return ResponseEntity.ok("뉴스가 등록되었습니다. ID: " + newsNum);
    }
    

    // 언론보도 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/UpdateNews/{newsNum}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateNews(
            @PathVariable("newsNum") Long newsNum, //newsNum 기준으로 뉴스 조회
            @RequestPart("dto") NewsUpdateRegisterDTO dto) { //noticeDto 데이터로 수정 반영
    	System.out.println("언론보도 수정 조회!!");
        
        newsService.updateNews(newsNum, dto);
        return ResponseEntity.ok("뉴스가 수정되었습니다.");
    }

    // 언론보도 삭제(단일)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/DeleteNews/{newsNum}")
    public ResponseEntity<String> deleteNews(@PathVariable("newsNum") Long newsNum) {
    	System.out.println("언론보도 삭제(단일) 조회!!");
        newsService.deleteNews(newsNum);
        return ResponseEntity.ok("뉴스가 삭제되었습니다.");
    }

    // 언론보도 삭제(일괄)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/DeleteNewsByIds")
    public ResponseEntity<String> deleteNewsByIds(@RequestBody List<Long> newsNums) {
    	System.out.println("언론보도 삭제(일괄) 조회!!");
        newsService.deleteNewsByIds(newsNums);
        return ResponseEntity.ok("뉴스가 일괄 삭제되었습니다.");
    }
    
}