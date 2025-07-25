package com.EduTech.dto.qna;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionDTO {
	// 문의 사항 조회 질문 dto
	private String title; // 제목
	private String content; // 내용
	private Boolean state; // 공개여부
	private LocalDateTime createdAt; // 작성일
	private Long view; // 조회수
	private String memId; // 회원 아이디
	private List<AnswerDTO> answerList; // 질문에 대한 답변들
}
