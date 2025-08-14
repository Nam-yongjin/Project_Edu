package com.EduTech.controller.qna;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.qna.AnswerUpdateDTO;
import com.EduTech.dto.qna.AnswerWriteDTO;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.service.qna.AnswerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answer")
public class AnswerController {
	private final AnswerService answerService;
	
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/addAnswer")
	public ResponseEntity<String> addAnswer(@RequestBody AnswerWriteDTO answerWriteDTO)
	{
		String memId = JWTFilter.getMemId();
		answerService.addAnswer(answerWriteDTO,memId);
		return ResponseEntity.ok("답변 추가 성공");
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/updateAnswer")
	public ResponseEntity<String> updateAnswer(@RequestBody AnswerUpdateDTO answerUpdateDTO)
	{
		String memId = JWTFilter.getMemId();
		answerService.updateAnswer(answerUpdateDTO,memId);
		return ResponseEntity.ok("답변 수정 성공");
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/deleteAnswer/{answerNum}")
	public ResponseEntity<String> deleteAnswer(@PathVariable("answerNum") Long answerNum) {
		String memId = JWTFilter.getMemId();
	    answerService.deleteAnswer(answerNum,memId);
	    return ResponseEntity.ok("삭제 성공");
	}

}





