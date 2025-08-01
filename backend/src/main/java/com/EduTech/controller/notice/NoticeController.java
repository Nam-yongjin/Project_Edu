package com.EduTech.controller.notice;

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

import com.EduTech.dto.notice.NoticeCreateRegisterDTO;
import com.EduTech.dto.notice.NoticeDetailDTO;
import com.EduTech.dto.notice.NoticeListDTO;
import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.dto.notice.NoticeUpdateRegisterDTO;
import com.EduTech.entity.notice.NoticeFile;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.service.notice.NoticeService;
import com.EduTech.util.FileUtil;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {

	private final NoticeService noticeService;
	private final NoticeFileRepository noticeFileRepository;
	private final FileUtil fileUtil;

	// 일반 회원

	// 공지사항 전체 조회(검색, 페이징)
	@GetMapping("/NoticeList")
    public ResponseEntity<Page<NoticeListDTO>> getNoticeList(@ModelAttribute NoticeSearchDTO searchDTO) {
    	
        return ResponseEntity.ok(noticeService.getNoticeList(searchDTO));
    }

    // 공지사항 상세 조회(조회수 증가)
    @GetMapping("/{noticeNum}")
    public ResponseEntity<NoticeDetailDTO> getNoticeDetail(@PathVariable Long noticeNum) {
    	System.out.println("콘솔에 나오는지 체크(디테일)");
        noticeService.increaseViewCount(noticeNum);
        return ResponseEntity.ok(noticeService.getNoticeDetail(noticeNum));
    }

    // 고정 공지사항 조회
    @GetMapping("/pinned")
    public ResponseEntity<List<NoticeListDTO>> getPinnedNotice() {
        return ResponseEntity.ok(noticeService.getPinnedNotices());
    }

    // 관리자 전용

    // 공지사항 등록
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/AddNotice")
    public ResponseEntity<String> createNotice(
            @RequestPart("dto") NoticeCreateRegisterDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        
        // DTO에 파일 설정
        if (files != null) {
            dto.setFiles(files);
        }
        
        Long noticeNum = noticeService.createNotice(dto);
        return ResponseEntity.ok("공지사항이 등록되었습니다. ID: " + noticeNum);
    }

    // 공지사항 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/UpdateNotice/{noticeNum}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateNotice(
            @PathVariable Long noticeNum,
            @RequestPart("dto") NoticeUpdateRegisterDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        
        // DTO에 새 파일들 설정
        if (files != null) {
            dto.setNewFiles(files);
        }
        
        noticeService.updateNotice(noticeNum, dto);
        return ResponseEntity.ok("공지사항이 수정되었습니다.");
    }

    // 공지사항 삭제(단일)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/DeleteNotice/{noticeNum}")
    public ResponseEntity<String> deleteNotice(@PathVariable Long noticeNum) {
        noticeService.deleteNotice(noticeNum);
        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }

    // 공지사항 삭제(일괄)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/DeleteNotices")
    public ResponseEntity<String> deleteNotices(@RequestBody List<Long> noticeNums) {
        noticeService.deleteNotices(noticeNums);
        return ResponseEntity.ok("공지사항이 일괄 삭제되었습니다.");
    }

    // 파일 다운로드
    @GetMapping("/files/{fileId}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId) {
        try {
            // NoticeFileRepository를 통해 파일 정보 조회
            NoticeFile noticeFile = noticeFileRepository.findById(fileId)
                    .orElseThrow(() -> new EntityNotFoundException("파일을 찾을 수 없습니다. ID: " + fileId));
            
            // FileUtil을 사용하여 파일 다운로드
            ResponseEntity<Resource> response = fileUtil.getFile(noticeFile.getFilePath(), null);
            
            // 파일명을 한글로 다운로드하기 위한 헤더 설정
            String encodedFileName = URLEncoder.encode(noticeFile.getOriginalName(), StandardCharsets.UTF_8)
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