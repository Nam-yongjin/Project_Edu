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
	private Long demNum;
	private String demName;
	private Long itemNum;
	private String demMfr;
	private LocalDate expDate;
	private LocalDate regDate;
	private DemonstrationState state;
	//private List<DemonstrationImageDTO> imageList; 
	// 물품 대여 조회 페이지용 생성자
}
