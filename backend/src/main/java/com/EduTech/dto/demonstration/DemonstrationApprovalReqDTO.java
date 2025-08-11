package com.EduTech.dto.demonstration;

import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.RequestType;

import lombok.Data;

@Data
public class DemonstrationApprovalReqDTO {
	Long demRevNum; // 실증 신청 번호
	DemonstrationState state; // 실증 신청 상태
	RequestType type; // 반납/연기 여부
}
