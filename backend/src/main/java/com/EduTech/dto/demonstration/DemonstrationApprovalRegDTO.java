package com.EduTech.dto.demonstration;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;

@Data
public class DemonstrationApprovalRegDTO {	// 관리자가 실증 등록 창에서 승인 / 거부 버튼 클릭 시 상태 수정 (프론트->백)
	Long demRegNum; // 실증 등록 번호
	DemonstrationState demonstrationState; // 실증 등록 상태
}
