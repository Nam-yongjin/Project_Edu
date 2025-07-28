package com.EduTech.controller.notice;

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

import com.EduTech.dto.notice.NoticeCreateRegisterDTO;
import com.EduTech.dto.notice.NoticeDetailDTO;
import com.EduTech.dto.notice.NoticeListDTO;
import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.dto.notice.NoticeUpdateRegisterDTO;
import com.EduTech.service.notice.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notice")
public class NoticeController {
	
	private final NoticeService noticeService;
	
	//일반 회원
	
	//공지사항 전체 조회(검색, 페이징)
	@GetMapping("/NoticeList")
	public ResponseEntity<Page<NoticeListDTO>> getNoticeList(@ModelAttribute NoticeSearchDTO searchDTO,
			@PageableDefault(size = 10, sort = "postedAt", direction = Sort.Direction.DESC) Pageable pageable) {
		return ResponseEntity.ok(noticeService.getNoticeList(searchDTO, pageable));
	}
	
	//공지사항 상세 조회(조회수 증가)
	@GetMapping("/NoticeDetail")
	public ResponseEntity<NoticeDetailDTO> getNoticeDetail(@PathVariable Long noticeNum) {
		noticeService.increaseView(noticeNum); //클릭 시 조회수 증가
		return ResponseEntity.ok(noticeService.getNoticeDetail(noticeNum));
	}
	
	//고정 공지사항 조회
	@GetMapping("/pinned")
	public ResponseEntity<List<NoticeListDTO>> getPinnedNotice() {
		return ResponseEntity.ok(noticeService.findPinned());
	}
	
	//관리자 전용
	
	//공지사항 등록
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/AddNotice")
	public ResponseEntity<String> createNotice(@ModelAttribute NoticeCreateRegisterDTO dto,
			@RequestPart(required = false) List<MultipartFile> file) {
		noticeService.createNotice(dto, file);
		return ResponseEntity.ok("공지사항이 등록되었습니다.");
	}
	
	//공지사항 수정
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/UpdateNotice")
	public ResponseEntity<String> updateNotice(@PathVariable Long noticeNum,
			@ModelAttribute NoticeUpdateRegisterDTO dto,
			@RequestPart(required = false) List<MultipartFile> file) {
		noticeService.updateNotice(dto, file, noticeNum);
		return ResponseEntity.ok("공지사항이 수정되었습니다.");
	}
	
	//공지사항 삭제(단일)
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/DeleteNotice")
	public ResponseEntity<String> deleteNotice(@PathVariable Long noticeNum) {
		noticeService.deleteNotice(noticeNum);
		return ResponseEntity.ok("공지사항이 삭제되었습니다.");
	}
	
	//공지사항 삭제(일괄)
		@PreAuthorize("hasRole('ADMIN')")
		@DeleteMapping("/DeleteNotices")
		public ResponseEntity<String> deleteNotices(@RequestBody List<Long> noticeNums) {
			noticeService.deleteNotices(noticeNums);
			return ResponseEntity.ok("공지사항이 일괄 삭제되었습니다.");
		}
	
		

}
