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
	private LocalDate applyAt; // 신청 날짜
	private LocalDate startDate; // 대여 시작 날찌
	private LocalDate endDate; // 대여 끝 날찌
	private DemonstrationState state; // 상태값 
	private String memId; // 회원 아이디
	private String schoolName; // 학교 이름
	private String demName; // 상품 이름
	private String addr; // 주소
	private String addrDetail; // 상세 주소
	private String phone; // 핸드폰 번호
	private Long bItemNum; // 대여 갯수
	private Long demNum; // 상품 번호
	private List<ResRequestDTO> requestDTO; // 요청 리스트
	private DemonstrationImageDTO mainImage; // 메인 이미지
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
