package com.EduTech.service.qna;

import java.time.LocalDateTime;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.qna.AnswerUpdateDTO;
import com.EduTech.dto.qna.AnswerWriteDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.qna.Answer;
import com.EduTech.entity.qna.Question;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.qna.AnswerRepository;
import com.EduTech.repository.qna.QuestionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AnswerServiceImpl implements AnswerService {
	private final AnswerRepository answerRepository;
	private final QuestionRepository questionRepository;
	private final MemberRepository memberRepository;
	// 관리자가 답변글 추가할때 사용하는 기능
	public void addAnswer(AnswerWriteDTO answerWriteDTO,String memId) {
		
		 if (!"admin".equals(memId)) { // 관리자일 경우에만 수정 가능
		        throw new AccessDeniedException("관리자만 답변을 달수 있습니다.");
		    }
		 
		Answer answer=new Answer();
		answer.setContent(answerWriteDTO.getContent());
		Question question= questionRepository.findById(answerWriteDTO.getQuestionNum())
			    .orElseThrow(() -> new EntityNotFoundException("해당 질문이 존재하지 않습니다."));
		answer.setQuestion(question);
		Member member = memberRepository.findById(memId)
			    .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
		answer.setMember(member);
		answerRepository.save(answer);
	}
	
	// 관리자가 답변 글 수정
	public void updateAnswer(AnswerUpdateDTO answerUpdateDTO, String memId) {
		
	    if (!"admin".equals(memId)) { // 관리자일 경우에만 수정 가능
	        throw new AccessDeniedException("관리자만 수정할 수 있습니다.");
	    }

	    Answer answer = answerRepository.findById(answerUpdateDTO.getAnswerNum())
	            .orElseThrow(() -> new EntityNotFoundException("답변이 존재하지 않습니다."));

	    answerRepository.updateAnswer(answerUpdateDTO.getContent(), answer.getAnswerNum(),LocalDateTime.now());
	}

	// 관리자가 답변 글 삭제
	public void deleteAnswer(Long answerNum, String memId) {
		
	    if (!"admin".equals(memId)) { // 관리자일 경우에만 삭제 가능
	        throw new AccessDeniedException("관리자만 삭제할 수 있습니다.");
	    }

	    Answer answer = answerRepository.findById(answerNum)
	            .orElseThrow(() -> new EntityNotFoundException("답변이 존재하지 않습니다."));

	    answerRepository.deleteById(answerNum);
	}

	
}
