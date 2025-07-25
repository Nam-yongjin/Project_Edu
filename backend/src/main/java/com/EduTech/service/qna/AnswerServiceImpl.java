package com.EduTech.service.qna;

import org.modelmapper.ModelMapper;
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
	private final ModelMapper modelMapper;
	private final AnswerRepository answerRepository;
	private final QuestionRepository questionRepository;
	private final MemberRepository memberRepository;
	// 관리자가 답변글 추가할때 사용하는 기능
	public void addAnswer(AnswerWriteDTO answerWriteDTO) {
		Answer answer=modelMapper.map(answerWriteDTO,Answer.class);
		answer.setAnswerNum(null); // 기본키가 들어가므로 null 처리해준다
		Member member = memberRepository.findById(answerWriteDTO.getMemId())
			    .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
		answer.setMember(member);
		Question question=questionRepository.getById(answerWriteDTO.getQuestionNum());
		answer.setQuestion(question);
		answerRepository.save(answer);
	}
	
	// 관리자가 답변 글 수정할때 사용하는 기능
	public void updateAnswer(AnswerUpdateDTO answerUpdateDTO) {
		answerRepository.updateAnswer(answerUpdateDTO.getContent(), answerUpdateDTO.getAnswerNum());
	}

	// 관리자가 답변 글 삭제할때 사용하는 기능
	public void deleteAnswer(Long answerNum) {
		answerRepository.deleteById(answerNum);
	}
	
}
