package com.EduTech.dto.qna;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO {
	// 문의사항 조회 관리자 답변
		private LocalDateTime createdAt; // 답변 작성일
		private String content; // 답변 내용
		private Long answerNum; // 답변 글 번호
		private LocalDateTime updatedAt; // 수정일'
		private Long questionNum;
		private String memId; 
		public AnswerDTO(LocalDateTime createdAt, String content, Long answerNum, 
                LocalDateTime updatedAt, Long questionNum) {
    this.createdAt = createdAt;
    this.content = content;
    this.answerNum = answerNum;
    this.updatedAt = updatedAt;
    this.questionNum = questionNum;
}
}
