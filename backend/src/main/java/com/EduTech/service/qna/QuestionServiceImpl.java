package com.EduTech.service.qna;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.qna.QuestionDTO;
import com.EduTech.dto.qna.QuestionUpdateDTO;
import com.EduTech.dto.qna.QuestionWriteDTO;
import com.EduTech.dto.qna.SearchDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.qna.Question;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.qna.AnswerRepository;
import com.EduTech.repository.qna.QnASpecs;
import com.EduTech.repository.qna.QuestionRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class QuestionServiceImpl implements QuestionService {
	private final QuestionRepository questionRepository;
	private final AnswerRepository answerRepository;
	private final ModelMapper modelMapper;
	private final MemberRepository memberRepository;
	public PageResponseDTO<QuestionDTO> QnAView(SearchDTO searchDTO,Integer pageCount) {
		List<QuestionDTO> QPage=new ArrayList<>();
		Specification<Question> spec = Specification.where(null);
		
		if(searchDTO.getMemId()!=null&&!searchDTO.getMemId().isBlank())
		{
			spec = spec.and(QnASpecs.memIdContains(searchDTO.getMemId()));
		}
		else if(searchDTO.getTitle()!=null&&!searchDTO.getTitle().isBlank())
		{
			spec=spec.and(QnASpecs.titleContains(searchDTO.getTitle()));
		}
		Pageable pageable = PageRequest.of(pageCount, 10, Sort.by("questionNum").descending());
		Page<Question> questionPage = questionRepository.findAll(spec, pageable);	
		
		for(Question question:questionPage)
		{
			QuestionDTO questionDTO=modelMapper.map(question,QuestionDTO.class);
			questionDTO.setAnswerList(answerRepository.selectAnswer(question.getQuestionNum()));
			QPage.add(questionDTO);
		}
	
		return new PageResponseDTO<QuestionDTO>(QPage,questionPage.getTotalPages(),questionPage.getNumber());
	}
	
	// 회원이 질문 글 추가할때 사용하는 기능
	public void addQuestion(QuestionWriteDTO questionWriteDTO) {
		Question question=modelMapper.map(questionWriteDTO, Question.class);
		Member member = memberRepository.findById(questionWriteDTO.getMemId())
			    .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
		question.setMember(member);
		 question.setView(0L);
		 question.setAnswer(new ArrayList<>());
		questionRepository.save(question);
	}
	
	// 회원이 질문 글 수정할때 사용하는 기능
	public void updateQuestion(QuestionUpdateDTO questionUpdateDTO) {
		questionRepository.updateQuestion(questionUpdateDTO.getTitle(),questionUpdateDTO.getContent(),questionUpdateDTO.getState(),questionUpdateDTO.getQuestionNum());
	}
	
	// 회원이 질문 글 삭제할때 사용하는 기능
	public void deleteQuestion(Long questionNum) {
		questionRepository.deleteById(questionNum);
	}
}
