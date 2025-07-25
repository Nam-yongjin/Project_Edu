package com.EduTech.controller.qna;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.EduTech.dto.demonstration.DemonstrationApprovalResDTO;
import com.EduTech.service.news.NewsService;
import com.EduTech.service.qna.AnswerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/answer")
public class AnswerController {
	private final AnswerService answerService;
	

}





