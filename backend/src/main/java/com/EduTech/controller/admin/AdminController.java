package com.EduTech.controller.admin;

import java.util.List;

import org.springframework.core.io.Resource;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.admin.AdminMemberViewReqDTO;
import com.EduTech.dto.admin.AdminMemberViewResDTO;
import com.EduTech.dto.admin.AdminMessageDTO;
import com.EduTech.dto.admin.MemberStateChangeDto;
import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationSearchDTO;
import com.EduTech.entity.admin.BannerImage;
import com.EduTech.service.admin.AdminService;
import com.EduTech.util.FileUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
	private final AdminService adminService;
	private final FileUtil fileUtil;

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

	// 관리자가 회원 정보 조회하는 기능 (+정렬기준, 정렬방향 추가)
	// 기본정렬기준: 가입일, 기본정렬방향: 내림차순
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/members")
	public PageResponseDTO<AdminMemberViewResDTO> adminViewMembers(AdminMemberViewReqDTO adminMemberViewDTO,
			@RequestParam("pageCount") Integer pageCount,
			@RequestParam(value = "sortField", defaultValue = "createdAt") String sortField,
			@RequestParam(value = "sortDirection", defaultValue = "DESC") String sortDirection) {
		adminMemberViewDTO.setSortField(sortField);
		adminMemberViewDTO.setSortDirection(sortDirection);
		PageResponseDTO<AdminMemberViewResDTO> members = adminService.adminViewMembers(adminMemberViewDTO, pageCount);
		return members;
	}

	// 관리자가 회원 상태 수정하는 기능
	// 블랙리스트, 삭제 등등..
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/MemberStateChange")
	public ResponseEntity<String> memberStateChange(@RequestBody MemberStateChangeDto memberStateChangeDto) {
		adminService.MemberStateChange(memberStateChangeDto.getMemId(), memberStateChangeDto.getState());
		return ResponseEntity.ok("회원 상태 수정 완료");
	}

	// 배너 이미지 업로드
	@PostMapping("/banner/upload")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<BannerImage>> uploadBanners(@RequestParam("files") List<MultipartFile> files) {
		List<BannerImage> banners = adminService.uploadBanners(files);
		return ResponseEntity.ok(banners);
	}

	// 모든 배너 목록 조회
	@GetMapping("/banner/list")
	public ResponseEntity<List<BannerImage>> getAllBanners() {
		List<BannerImage> banners = adminService.getAllBanners();
		return ResponseEntity.ok(banners);
	}

	// 배너 삭제
	@DeleteMapping("/banner/delete/{bannerNum}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteBanner(@PathVariable("bannerNum") Long bannerNum) {
		adminService.deleteBanner(bannerNum);
		return ResponseEntity.ok("배너가 삭제되었습니다.");
	}

	// 배너 순서 수정
	@PutMapping("/banner/sequence")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> updateBannerSequence(@RequestBody List<Long> bannerNums) {
		adminService.updateBannerSequence(bannerNums);
		return ResponseEntity.ok("배너 순서가 변경되었습니다.");
	}
	
	// 배너 이미지 출력
	@GetMapping("/banner/view")
    public ResponseEntity<Resource> viewFile(@RequestParam("filePath") String filePath) {
        return fileUtil.getFile(filePath, "");
    }
	
	// 교사 실증 신청 조회
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/demRes")
	public PageResponseDTO<DemonstrationListReserveDTO> getAllDemResPage(@ModelAttribute DemonstrationSearchDTO demonstrationSearchDTO) {

		PageResponseDTO<DemonstrationListReserveDTO> AllDemRes = adminService.getAllDemRes(demonstrationSearchDTO);
		return AllDemRes;
	}

	// 기업 실증 신청 조회
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/demReg")
	public PageResponseDTO<DemonstrationListRegistrationDTO> getAllDemRegPage(@ModelAttribute DemonstrationSearchDTO demonstrationSearchDTO) {
		PageResponseDTO<DemonstrationListRegistrationDTO> AllDemReg = adminService.getAllDemReg(demonstrationSearchDTO);
		return AllDemReg;
	}
}
