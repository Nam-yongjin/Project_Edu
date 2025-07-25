package com.EduTech.dto.qna;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerWriteDTO {
	// 관리자 답변 작성 받을 DTO
		private String content; // 답변 내용
		private String memId; // 답변자 아이디
		private Long questionNum; // 질문글번호
}
