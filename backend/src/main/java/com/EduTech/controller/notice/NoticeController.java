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
		System.out.println("공지사항 전체 조회!!");
        return ResponseEntity.ok(noticeService.getNoticeList(searchDTO));
    }

    // 공지사항 상세 조회(조회수 증가)
	@GetMapping("/NoticeDetail/{noticeNum}")
	public ResponseEntity<NoticeDetailDTO> getNoticeDetail(@PathVariable("noticeNum") Long noticeNum) {
    	System.out.println("공지사항 상세 조회!!");
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
            @RequestPart("dto") NoticeCreateRegisterDTO dto, //프론트에서 dto를 JSON 문자열로 변환해서 FormData에 넣어줘야 함
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    	System.out.println("공지사항 등록!!");
//    	System.out.println("등록 파일 수: " + dto.getFiles().size());
    	
    	int fileCount = (dto.getFiles() != null) ? dto.getFiles().size() : 0;
        System.out.println("DTO 파일 수: " + fileCount);
        
        // DTO에 파일 설정
//        if (files != null) {
//            dto.setFiles(files);
//        }
        if (files != null) {
            dto.setFiles(files);
            System.out.println("실제 업로드된 파일 수: " + files.size());
        } else {
            System.out.println("업로드된 파일 없음");
        }
        
        Long noticeNum = noticeService.createNotice(dto);
        return ResponseEntity.ok("공지사항이 등록되었습니다. ID: " + noticeNum);
    }

    // 공지사항 수정
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping(value = "/UpdateNotice/{noticeNum}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)//noticeUpdate API가 멀티파트 요청만 받게 설정
    public ResponseEntity<String> updateNotice(			//@PathVariable은 상세 조회, 수정, 삭제 같은 작업에서 리소스 식별자로 사용
            @PathVariable("noticeNum") Long noticeNum, //@PathVariable에 이름 꼭 명시해줘야 함!!!! --> ("noticeNum") 이런 식으로
            @RequestPart("dto") NoticeUpdateRegisterDTO dto, //@RequestPart --> JSON과 파일을 분리해서 받음
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
    	System.out.println("공지사항 수정!!");
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
    public ResponseEntity<String> deleteNotice(@PathVariable("noticeNum") Long noticeNum) {
    	System.out.println("공지사항 삭제(단일)!!");
        noticeService.deleteNotice(noticeNum);
        return ResponseEntity.ok("공지사항이 삭제되었습니다.");
    }

    // 공지사항 삭제(일괄)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/DeleteNotices")
    public ResponseEntity<String> deleteNotices(@RequestBody List<Long> noticeNums) {
    	System.out.println("공지사항 삭제(일괄)!!");
        noticeService.deleteNotices(noticeNums);
        return ResponseEntity.ok("공지사항이 일괄 삭제되었습니다.");
    }

    // 파일 다운로드
    @GetMapping("/files/{notFileNum}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable("notFileNum") Long notFileNum) {
    	System.out.println("공지사항 파일 다운로드!!");
    	 System.out.println("=== 다운로드 API 호출됨 ===");
    	    System.out.println("요청된 notFileNum: " + notFileNum);
        try {
        	 // notFileNum으로 조회
            NoticeFile noticeFile = noticeFileRepository.findById(notFileNum)
                .orElseThrow(() -> new EntityNotFoundException("파일을 찾을 수 없습니다. ID: " + notFileNum));
            
            // FileUtil을 사용하여 파일 다운로드
            ResponseEntity<Resource> response = fileUtil.getFile(noticeFile.getFilePath());
            
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
    
    //이미지 보기
    @GetMapping("/view/{fileName}")
    public ResponseEntity<Resource> viewFile(@PathVariable("fileName") String fileName) {
    	System.out.println("공지사항 이미지 보기!!");
    	try {
    		//noticeFiles에서 파일 찾기
    		String filePath = "notice-files/" + fileName;
    		return fileUtil.getFile(filePath);
    		
    	} catch (Exception e) {
    		//파일을 찾을 수 없으면 기본 이미지 반환
    		return fileUtil.getFile("default.png");
    	}
    }
    
    
    
}