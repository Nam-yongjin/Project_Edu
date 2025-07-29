package com.EduTech.controller.news;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;
import com.EduTech.service.news.NewsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {
	
private final NewsService newsService;
	
	//일반 회원
	
	//언론보도 전체 조회(검색, 페이징)
	@GetMapping("/NewsList")
	public ResponseEntity<Page<NewsListDTO>> getNewsList(@ModelAttribute NewsSearchDTO searchDTO,
			@PageableDefault(size = 10, sort = "created_at", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(newsService.getNewsList(searchDTO, pageable));
	}
	
	//언론보도 상세 조회(조회수 증가)
	@GetMapping("/NewsDetail")
	public ResponseEntity<NewsDetailDTO> getNewsDetail(@PathVariable Long id) {
		newsService.increaseView(id); //클릭 시 조회수 증가
		return ResponseEntity.ok(newsService.getNewsDetail(id));
	}
	
	//관리자 전용
	
	//언론보도 등록
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/AddNews")
	public ResponseEntity<String> createNews(@ModelAttribute NewsCreateRegisterDTO dto,
			@RequestPart(required = false) List<MultipartFile> file) {
		newsService.createNews(dto, file);
		return ResponseEntity.ok("뉴스가 등록되었습니다.");
	}
	
	//언론보도 수정
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/UpdateNews")
	public ResponseEntity<String> updateNews(@PathVariable Long id,
			@ModelAttribute NewsUpdateRegisterDTO dto,
			@RequestPart(required = false) List<MultipartFile> file) {
		newsService.updateNews(dto, file, id);
		return ResponseEntity.ok("뉴스가 수정되었습니다.");
	}
	
	//언론보도 삭제(단일)
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/DeleteNews")
	public ResponseEntity<String> deleteNews(@PathVariable Long id) {
		newsService.deleteNews(id);
		return ResponseEntity.ok("뉴스가 삭제되었습니다.");
	}
	
	//언론보도 삭제(일괄)
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/DeleteNewsByIds")
	public ResponseEntity<String> deleteNewsByIds(@RequestBody List<Long> ids) {
		newsService.deleteNewsByIds(ids);
		return ResponseEntity.ok("뉴스가 일괄 삭제되었습니다.");
	}

}
