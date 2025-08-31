package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemonstrationListReserveDTO { // 관리자 물품 대여 조회

	private Long demRevNum; // demonstration_reserve 테이블의 기본키
	private LocalDate applyAt;
	private LocalDate startDate;
	private LocalDate endDate;
	private DemonstrationState state;
	private String memId; // 회원 아이디
	private String schoolName; // 학교 이름
	private String demName;
	private String addr;
	private String addrDetail;
	private String phone;
	private Long bItemNum;
	private Long demNum;
	private List<ResRequestDTO> requestDTO;

	// private List<DemonstrationImageDTO> imageList=new ArrayList<>();
	public DemonstrationListReserveDTO(Long demRevNum, LocalDate applyAt, LocalDate startDate, LocalDate endDate,
			DemonstrationState state, String memId, String schoolName, String demName, String addr, String addrDetail,
			String phone, Long bItemNum, Long demNum) {
		this.demRevNum = demRevNum;
		this.applyAt = applyAt;
		this.startDate = startDate;
		this.endDate = endDate;
		this.state = state;
		this.memId = memId;
		this.schoolName = schoolName;
		this.demName = demName;
		this.addr = addr;
		this.addrDetail = addrDetail;
		this.phone = phone;
		this.bItemNum = bItemNum;
		this.demNum = demNum;
	}
}
