package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.web.bind.annotation.RequestParam;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DemonstrationSearchDTO {
	private String search; // 검색어
	private String type; // 검색 칼럼명
	 private String sortBy; // 정렬 칼럼명
	 private String sort; // 정렬 방식
	 private Integer pageCount; // 페이지 번호
	 private String statusFilter; // 상태값
	 private Long demNum; // 상품 번호
}
