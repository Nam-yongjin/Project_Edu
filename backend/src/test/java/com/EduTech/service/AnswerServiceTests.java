package com.EduTech.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.EduTech.dto.qna.AnswerUpdateDTO;
import com.EduTech.dto.qna.AnswerWriteDTO;
import com.EduTech.service.qna.AnswerService;

@SpringBootTest
public class AnswerServiceTests {
	@Autowired
	AnswerService answerService;
	//@Test 
	@DisplayName("답변 추가 테스트")
	public void addAnswer()
	{
		AnswerWriteDTO answerWriteDTO=new AnswerWriteDTO();
		answerWriteDTO.setContent("ㅋㅋㅋ");
		answerWriteDTO.setMemId("user2");
		answerWriteDTO.setQuestionNum(1L);
		answerService.addAnswer(answerWriteDTO);
	}
	
	//@Test
	@DisplayName("답변 수정 테스트")
	public void updateQuestionTest() {
		AnswerUpdateDTO answerUpdateDTO =new AnswerUpdateDTO();
		answerUpdateDTO.setAnswerNum(3L);
		answerUpdateDTO.setContent("수정된 답글");
		answerService.updateAnswer(answerUpdateDTO);
	}
	
	//@Test
	@DisplayName("답변 삭제 테스트")
	public void deleteQuestionTest() {
		Long answerNum=3L;
		answerService.deleteAnswer(answerNum);
	}
}
