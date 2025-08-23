package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.RequestType;

import lombok.Data;

@Data
public class DemonstrationApprovalReqDTO {
	List<Long> demRevNum; // 실증 신청 번호
	DemonstrationState state; // 실증 신청 상태
	RequestType type; // 반납/연기 여부
	LocalDate updateDate; // 업데이트할 날짜
}
