package com.EduTech.dto.qna;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionUpdateDTO {
	// 회원 질문 수정 받을 DTO
	private String title; // 질문 제목
	private String content; // 질문 내용
	private Boolean state; // 공개 여부
	private Long questionNum; // 질문 글 번호
}
