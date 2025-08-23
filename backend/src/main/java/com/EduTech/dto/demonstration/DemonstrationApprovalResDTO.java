package com.EduTech.dto.demonstration;

import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.Data;

@Data
public class DemonstrationApprovalResDTO { // 관리자가 실증 신청 창에서 승인 / 거부 버튼 클릭 시 상태 수정 (프론트->백)
	List<Long> demRevNum; // 실증 신청 번호
	DemonstrationState state; // 실증 신청 상태
}
