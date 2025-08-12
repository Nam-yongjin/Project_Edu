package com.EduTech.controller.qna;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.qna.QuestionDTO;
import com.EduTech.dto.qna.QuestionUpdateDTO;
import com.EduTech.dto.qna.QuestionWriteDTO;
import com.EduTech.dto.qna.SearchDTO;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.service.qna.QuestionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/question")
public class QuestionController {
	private final QuestionService questionService;
	@GetMapping("/QnAView")
	public void QnAView(@RequestBody SearchDTO searchDTO,Integer pageCount)
	{
	
		//PageResponseDTO<QuestionDTO> QnAView=questionService.QnAView(searchDTO, pageCount);
		
		
	}
	
	@GetMapping("/QnADetail")
	public void QnAViewDetail(@RequestParam("questionNum") Long questionNum)
	{
		questionService.QnAViewDetail(questionNum);
		
	}
	
	@PostMapping(value = "/addQuestion")
	public ResponseEntity<String> addQuestion(@RequestBody QuestionWriteDTO questionWriteDTO)
	{
		System.out.println(questionWriteDTO);
		String memId = JWTFilter.getMemId();
		questionService.addQuestion(questionWriteDTO,memId);
		return ResponseEntity.ok("질문 추가 성공");
	}
	
	@PutMapping("/updateQuestion")
	public ResponseEntity<String> updateQuestion(@RequestBody QuestionUpdateDTO questionUpdateDTO)
	{
		questionService.updateQuestion(questionUpdateDTO);
		return ResponseEntity.ok("질문 수정 성공");
		
	}
	
	@DeleteMapping("/deleteQuestion")
	public ResponseEntity<String> deleteQuestion(@RequestParam("questionNum") Long questionNum)
	{
		questionService.deleteQuestion(questionNum);
		return ResponseEntity.ok("질문 삭제 성공");
	}
}
