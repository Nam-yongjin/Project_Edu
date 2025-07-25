package com.EduTech.dto.qna;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
	private String memId; // 검색 창에서 받아올 아이디
	private String title; // 검색 창에서 받아올 제목
}
