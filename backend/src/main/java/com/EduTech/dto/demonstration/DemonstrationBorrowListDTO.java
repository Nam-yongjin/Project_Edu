package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemonstrationBorrowListDTO {
	private Long demNum; // 상품번호
	private String demName; // 상품이름
	private Long itemNum; // 재고량
	private String demMfr; // 제조사
	private LocalDate expDate; // 대여날짜
	private LocalDate regDate; // 등록날짜
	private DemonstrationState state; // 상태값
	private DemonstrationImageDTO mainImage; // 메인 이미지

	public DemonstrationBorrowListDTO(Long demNum, String demName, Long itemNum, String demMfr, LocalDate expDate,
			LocalDate regDate, DemonstrationState state) {
		this.demNum = demNum;
		this.demName = demName;
		this.itemNum = itemNum;
		this.demMfr = demMfr;
		this.expDate = expDate;
		this.regDate = regDate;
		this.state = state;
	}
}
