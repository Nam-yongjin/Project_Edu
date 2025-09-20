package com.EduTech.dto.qna;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
	private String search; // 검색 키워드
	private String type; // 검색 칼럼
	 private String sortBy; // 정렬 방식
	 private String sort; // 정렬 칼럼명
	 private Integer pageCount; // 페이지 번호
	 private LocalDate startDate; // 시작 날짜
	 private LocalDate endDate; // 끝 날짜
	 private String answered; // 답변 여부
}

