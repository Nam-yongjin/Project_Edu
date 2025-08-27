package com.EduTech.controller.qna;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
	
	// 문의 사항 글 조회
	@GetMapping("/QnAView")
	public PageResponseDTO<QuestionDTO> QnAView(@ModelAttribute SearchDTO searchDTO)
	{
		PageResponseDTO<QuestionDTO> QnAView=questionService.QnAView(searchDTO);
		return QnAView;
	}
	
	// 문의 사항 상세 글 조회
	@GetMapping("/QnADetail/{questionNum}")
	public QuestionDTO QnAViewDetail(@PathVariable("questionNum") Long questionNum)
	{
		QuestionDTO detail=questionService.QnAViewDetail(questionNum);
		return detail;
	}
	
	// 문의 사항 질문 글 추가
	@PreAuthorize("isAuthenticated()")
	@PostMapping(value = "/addQuestion")
	public ResponseEntity<String> addQuestion(@RequestBody QuestionWriteDTO questionWriteDTO)
	{
		String memId = JWTFilter.getMemId();
		questionService.addQuestion(questionWriteDTO,memId);
		return ResponseEntity.ok("질문 추가 성공");
	}
	
	// 문의 사항 질문 글 수정
	@PreAuthorize("isAuthenticated()")
	@PutMapping("/updateQuestion")
	public ResponseEntity<String> updateQuestion(@RequestBody QuestionUpdateDTO questionUpdateDTO)
	{
		 String memId = JWTFilter.getMemId();
		questionService.updateQuestion(questionUpdateDTO,memId);
		return ResponseEntity.ok("질문 수정 성공");
		
	}

	// 문의 사항 질문 글 삭제
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/deleteQuestions")
	public ResponseEntity<String> deleteQuestions(@RequestBody List<Long> questionNums)
	{
		 String memId = JWTFilter.getMemId();
		questionService.deleteQuestions(questionNums,memId);
		return ResponseEntity.ok("질문 삭제 성공");
	}
}
