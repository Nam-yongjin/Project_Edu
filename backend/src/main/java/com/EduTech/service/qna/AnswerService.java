package com.EduTech.service.qna;

import com.EduTech.dto.qna.AnswerUpdateDTO;
import com.EduTech.dto.qna.AnswerWriteDTO;

public interface AnswerService {
	// 관리자가 답변글 추가할때 사용하는 기능
		void addAnswer(AnswerWriteDTO answerWriteDTO);

		// 관리자가 답변 글 수정할때 사용하는 기능
		void updateAnswer(AnswerUpdateDTO answerUpdateDTO);

		// 관리자가 답변 글 삭제할때 사용하는 기능
		void deleteAnswer(Long answerNum);
}