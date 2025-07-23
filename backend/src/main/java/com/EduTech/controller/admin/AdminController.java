package com.EduTech.controller.admin;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.service.admin.AdminService;
import com.EduTech.service.demonstration.DemonstrationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {
	private final AdminService AdminService;
	@PutMapping("/ResState")
	public ResponseEntity<String> DemResStateChange(
			@RequestBody DemonstrationApprovalResDTO demonstrationApprovalResDTO) {
		AdminService.approveOrRejectDemRes(demonstrationApprovalResDTO);
		return ResponseEntity.ok("Res 상태 변경 성공");
	}

	// 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	@PutMapping("/RegState")
	public ResponseEntity<String> DemRegStateChange(
			@RequestBody DemonstrationApprovalRegDTO demonstrationApprovalRegDTO) {
		AdminService.approveOrRejectDemReg(demonstrationApprovalRegDTO);
		return ResponseEntity.ok("Reg 상태 변경 성공");
	}
}
