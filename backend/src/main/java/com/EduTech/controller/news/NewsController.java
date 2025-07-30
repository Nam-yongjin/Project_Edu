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
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;
import com.EduTech.entity.news.NewsFile;
import com.EduTech.repository.news.NewsFileRepository;
import com.EduTech.service.news.NewsService;
import com.EduTech.util.FileUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final NewsFileRepository newsFileRepository;
    private final FileUtil fileUtil;

    // 일반 회원

    // 언론보도 전체 조회(검색, 페이징)
    @GetMapping("/NewsList")
    public ResponseEntity<Page<NewsListDTO>> getNewsList(@ModelAttribute NewsSearchDTO searchDTO) {
        return ResponseEntity.ok(newsService.getNewsList(searchDTO));
    }

    // 언론보도 상세 조회(조회수 증가)
    @GetMapping("/NewsDetail/{newsNum}")
    public ResponseEntity<NewsDetailDTO> getNewsDetail(@PathVariable Long newsNum) {

        newsService.increaseViewCount(newsNum);
        return ResponseEntity.ok(newsService.getNewsDetail(newsNum));
    }

    // 관리자 전용

    // 언론보도 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/AddNews")
    public ResponseEntity<String> createNews(
            @RequestPart("dto") NewsCreateRegisterDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        
        // DTO에 파일 설정
        if (files != null) {
            dto.setFiles(files);
        }
        
        Long newsNum = newsService.createNews(dto);
        return ResponseEntity.ok("뉴스가 등록되었습니다. ID: " + newsNum);
    }

    // 언론보도 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/UpdateNews/{newsNum}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateNews(
            @PathVariable Long newsNum,
            @RequestPart("dto") NewsUpdateRegisterDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        
        // DTO에 새 파일들 설정
        if (files != null) {
            dto.setNewFiles(files);
        }
        
        newsService.updateNews(newsNum, dto);
        return ResponseEntity.ok("뉴스가 수정되었습니다.");
    }

    // 언론보도 삭제(단일)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/DeleteNews/{newsNum}")
    public ResponseEntity<String> deleteNews(@PathVariable Long newsNum) {
        newsService.deleteNews(newsNum);
        return ResponseEntity.ok("뉴스가 삭제되었습니다.");
    }

    // 언론보도 삭제(일괄)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/DeleteNewsByIds")
    public ResponseEntity<String> deleteNewsByIds(@RequestBody List<Long> newsNums) {
        newsService.deleteNewsByIds(newsNums);
        return ResponseEntity.ok("뉴스가 일괄 삭제되었습니다.");
    }

    // 파일 다운로드
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            // NoticeFileRepository를 통해 파일 정보 조회
            NewsFile newsFile = newsFileRepository.findById(fileId)
                    .orElseThrow(() -> new EntityNotFoundException("파일을 찾을 수 없습니다. ID: " + fileId));
            
            // FileUtil을 사용하여 파일 다운로드
            ResponseEntity<Resource> response = fileUtil.getFile(newsFile.getFilePath(), null);
            
            // 파일명을 한글로 다운로드하기 위한 헤더 설정
            String encodedFileName = URLEncoder.encode(newsFile.getOriginalName(), StandardCharsets.UTF_8)
                    .replaceAll("\\+", "%20");
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                           "attachment; filename*=UTF-8''" + encodedFileName)
                    .body(response.getBody());
                    
        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 중 오류가 발생했습니다.", e);
        }
    }
}