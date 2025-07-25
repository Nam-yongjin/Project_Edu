package com.EduTech.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.qna.QuestionDTO;
import com.EduTech.dto.qna.QuestionUpdateDTO;
import com.EduTech.dto.qna.QuestionWriteDTO;
import com.EduTech.dto.qna.SearchDTO;
import com.EduTech.service.qna.QuestionService;

@SpringBootTest
public class QuestionServiceTests {
	@Autowired
	QuestionService questionService;
	//@Test
	@DisplayName("질문 추가 테스트")
	public void addQuestionTest() {
		QuestionWriteDTO questionWriteDTO=new QuestionWriteDTO();
		questionWriteDTO.setTitle("세번째 질문입니다.");
		questionWriteDTO.setContent("질문 하기는 왜 이렇게 귀찮을까요 아 하기 싫다. 진짜 테스트");
		questionWriteDTO.setMemId("user5");
		questionWriteDTO.setState(false);
		questionService.addQuestion(questionWriteDTO);
		
	}
	
	//@Test
	@DisplayName("질문 수정 테스트")
	public void updateQuestionTest() {
		QuestionUpdateDTO questionUpdateDTO=new QuestionUpdateDTO();
		questionUpdateDTO.setContent("바뀐 수정 입니다.");
		questionUpdateDTO.setTitle("바뀐거라고");
		questionUpdateDTO.setState(false);
		questionUpdateDTO.setQuestionNum(1L);
		questionService.updateQuestion(questionUpdateDTO);
	}
	
	//@Test
	@DisplayName("질문 삭제 테스트")
	public void deleteQuestionTest() {
		Long answerNum =3L;
		questionService.deleteQuestion(answerNum);
	}
	
	@Test
	@DisplayName("QnA 조회 테스트")
	public void QnATest() {
		SearchDTO searchDTO=new SearchDTO();
		Integer pageCount=0;
		//searchDTO.setMemId("user5");
		PageResponseDTO<QuestionDTO> page=questionService.QnAView(searchDTO, pageCount);
		System.out.println(page);
	}
}

