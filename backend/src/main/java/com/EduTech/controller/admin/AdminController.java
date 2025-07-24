package com.EduTech.controller.admin;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;
import com.EduTech.dto.notice.NoticeCreateRegisterDTO;
import com.EduTech.dto.notice.NoticeUpdateRegisterDTO;
import com.EduTech.entity.member.MemberState;
import com.EduTech.service.admin.AdminService;
import com.EduTech.service.news.NewsService;
import com.EduTech.service.notice.NoticeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/admin")
public class AdminController {
	private final AdminService adminService;
	private final NoticeService noticeService;
	private final NewsService newsService;
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/ResState")
	public ResponseEntity<String> DemResStateChange(
			@RequestBody DemonstrationApprovalResDTO demonstrationApprovalResDTO) {
		adminService.approveOrRejectDemRes(demonstrationApprovalResDTO);
		return ResponseEntity.ok("Res 상태 변경 성공");
	}

	// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/RegState")
	public ResponseEntity<String> DemRegStateChange(
			@RequestBody DemonstrationApprovalRegDTO demonstrationApprovalRegDTO) {
		adminService.approveOrRejectDemReg(demonstrationApprovalRegDTO);
		return ResponseEntity.ok("Reg 상태 변경 성공");
	}

	// 관리자가 메시지 보내는 기능
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/sendMessage")
	public ResponseEntity<String> SendMessage(@ModelAttribute AdminMessageDTO adminMessageDTO) {
		adminService.sendMessageForUser(adminMessageDTO);
		return ResponseEntity.ok("메시지 전송 성공!");
	}

	// 관리자가 회원 정보 조회하는 기능
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/members")
	public PageResponseDTO<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewDTO,
			@RequestParam("pageCount") Integer pageCount) {
		PageResponseDTO<AdminMemberViewResDTO> members = adminService.adminViewMembers(adminMemberViewDTO, pageCount);
		return members;
	}

	// 관리자가 회원 상태 수정하는 기능
	// 블랙리스트, 삭제 등등..
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/MemberStateChange")
	public ResponseEntity<String> memberStateChange(@RequestParam("memId") List<String> memId,
			@RequestParam("state") MemberState state) {
		adminService.MemberStateChange(memId, state);
		return ResponseEntity.ok("회원 상태 수정 완료");
	}

	// 관리자 공지사항 글쓰기 기능
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/writeNotice")
	public ResponseEntity<String> writeNotice(NoticeCreateRegisterDTO dto, List<MultipartFile> file) {
		noticeService.createNotice(dto, file);
		return ResponseEntity.ok("글 등록 완료");
	}

	// 관리자 공지사항 수정 기능
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/updateNotice")
	public ResponseEntity<String> updateNotice(NoticeUpdateRegisterDTO dto, List<MultipartFile> file, Long noticeNum) {
		noticeService.updateNotice(dto, file, noticeNum);
		return ResponseEntity.ok("글 수정 완료");
	}

	// 관리자 공지사항 삭제 기능
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/deleteNotice")
	public ResponseEntity<String> deleteNotice(Long noticeNum) {
		deleteNotice(noticeNum);
		return ResponseEntity.ok("글 단일 삭제 완료");
	}

	// 관리자 공지사항 삭제 기능
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/deleteNotices")
	public ResponseEntity<String> deleteNotices(List<Long> noticeNums) {
		deleteNotices(noticeNums);
		return ResponseEntity.ok("글 일괄 삭제 완료");
	}

	// 관리자 언론 보도 등록 
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/writeNews")
	public ResponseEntity<String> writeNews(NewsCreateRegisterDTO dto, List<MultipartFile> file)
	{
		newsService.createNews(dto,file);
		return ResponseEntity.ok("언론 보도 등록 완료");
	}
	
	// 관리자 언론 보도 수정
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/updateNews")
	public ResponseEntity<String> updateNews(NewsUpdateRegisterDTO dto, List<MultipartFile> file, Long newsNum)
	{
		newsService.updateNews(dto,file,newsNum);
		return ResponseEntity.ok("언론 보도 수정 완료");
	}
	
	// 관리자 언론 보도 삭제(단일)
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/deleteNews")
	public ResponseEntity<String>deleteNews(Long newsNum)
	{
		newsService.deleteNews(newsNum);
		return ResponseEntity.ok("언론 보도 단일 삭제 완료");
	}
	
	// 관리자 언론 보도 삭제 (다수)
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(("/deleteNewsList"))
	public ResponseEntity<String>deleteNewsList(List<Long> newsNum)
	{
		newsService.deleteNewsByIds(newsNum);
		return ResponseEntity.ok("언론 보도 일괄 삭제 완료");
	}
	// 관리자 문의 사항 답변 기능(보류)

	// 행사 등록 (보류)

	// 좌석 등록 (보류)

}
