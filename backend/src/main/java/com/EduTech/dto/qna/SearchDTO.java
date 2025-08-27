package com.EduTech.dto.qna;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
	private String search;
	private String type;
	 private String sortBy;
	 private String sort;
	 private Integer pageCount;
	 private LocalDate startDate;
	 private LocalDate endDate;
	 private String answered;
}

