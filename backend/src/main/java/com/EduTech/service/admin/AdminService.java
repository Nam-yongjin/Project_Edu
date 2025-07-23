package com.EduTech.service.admin;

import com.EduTech.dto.demonstration.DemonstrationApprovalRegDTO;
import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;

public interface AdminService {
	void approveOrRejectDemRes(DemonstrationApprovalResDTO demonstrationApprovalResDTO); // 실증 교사 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
	void approveOrRejectDemReg(DemonstrationApprovalRegDTO demonstrationApprovalRegDTO); // 실증 기업 신청 조회에서 승인 / 거부 여부 받아와서 상태값 업데이트 기능
}