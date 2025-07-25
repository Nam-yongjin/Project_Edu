package com.EduTech.dto.qna;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerUpdateDTO {
	// 관리자 답변 수정 받을 DTO
		private String content; // 답변 내용
		private Long answerNum; // 답변 글 번호
}
