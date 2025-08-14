package com.EduTech.service.qna;

import java.util.List;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.qna.QuestionDTO;
import com.EduTech.dto.qna.QuestionUpdateDTO;
import com.EduTech.dto.qna.QuestionWriteDTO;
import com.EduTech.dto.qna.SearchDTO;

public interface QuestionService {
	// 문의 사항 페이지에서 질문을 기준으로 사용자 ID와 TITLE을 가져와 검색하여 PAGE를 받아오는 기능
		
		// 회원이 질문 글 추가할때 사용하는 기능
		void addQuestion(QuestionWriteDTO questionWriteDTO,String memId);
		
		// 회원이 질문 글 수정할때 사용하는 기능
		void updateQuestion(QuestionUpdateDTO questionUpdateDTO,String memId);
		
		// 여러개 글 삭제할때 사용하는 기능
		void deleteQuestions(List<Long> questionNums,String memId);
		
		// 문의 사항 글 상세 페이지 조회 기능
		QuestionDTO QnAViewDetail(Long questionNum);
		
		// 문의 사항 조회 기능
		PageResponseDTO<QuestionDTO> QnAView(SearchDTO searchDTO);
}