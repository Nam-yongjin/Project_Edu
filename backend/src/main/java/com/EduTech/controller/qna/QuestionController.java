package com.EduTech.controller.qna;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.qna.QuestionDTO;
import com.EduTech.dto.qna.QuestionUpdateDTO;
import com.EduTech.dto.qna.QuestionWriteDTO;
import com.EduTech.dto.qna.SearchDTO;
import com.EduTech.service.qna.AnswerService;
import com.EduTech.service.qna.QuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question")
public class QuestionController {
	private final QuestionService questionService;
	@GetMapping("/QnAView")
	public PageResponseDTO<QuestionDTO> QnAView(SearchDTO searchDTO,Integer pageCount)
	{
		PageResponseDTO<QuestionDTO> QnAView=questionService.QnAView(searchDTO, pageCount);
		return QnAView;
	}
	
	@PostMapping("/addQuestion")
	public ResponseEntity<String> addQuestion(QuestionWriteDTO questionWriteDTO)
	{
		questionService.addQuestion(questionWriteDTO);
		return ResponseEntity.ok("질문 추가 성공");
	}
	
	@PutMapping("/updateQuestion")
	public ResponseEntity<String> updateQuestion(QuestionUpdateDTO questionUpdateDTO)
	{
		questionService.updateQuestion(questionUpdateDTO);
		return ResponseEntity.ok("질문 수정 성공");
		
	}
	
	@DeleteMapping("/deleteQuestion")
	public ResponseEntity<String> deleteQuestion(Long questionNum)
	{
		questionService.deleteQuestion(questionNum);
		return ResponseEntity.ok("질문 삭제 성공");
	}
}
