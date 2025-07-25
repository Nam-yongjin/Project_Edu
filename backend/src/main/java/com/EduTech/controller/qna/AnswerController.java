package com.EduTech.controller.qna;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.qna.AnswerUpdateDTO;
import com.EduTech.dto.qna.AnswerWriteDTO;
import com.EduTech.service.qna.AnswerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answer")
public class AnswerController {
	private final AnswerService answerService;
	@PostMapping("/addAnswer")
	public ResponseEntity<String> addAnswer(AnswerWriteDTO answerWriteDTO)
	{
		answerService.addAnswer(answerWriteDTO);
		return ResponseEntity.ok("답변 추가 성공");
	}
	
	@PutMapping("/updateAnswer")
	public ResponseEntity<String> updateAnswer(AnswerUpdateDTO answerUpdateDTO)
	{
		answerService.updateAnswer(answerUpdateDTO);
		return ResponseEntity.ok("답변 수정 성공");
	}
	
	@DeleteMapping("/deleteAnswer")
	public ResponseEntity<String> deleteAnswer(Long answerNum)
	{
		answerService.deleteAnswer(answerNum);
		return ResponseEntity.ok("답변 삭제 성공");
		
	}

}





