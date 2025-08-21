package com.EduTech.service.qna;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.Page.PageResponseDTO;
import com.EduTech.dto.qna.AnswerDTO;
import com.EduTech.dto.qna.QuestionDTO;
import com.EduTech.dto.qna.QuestionUpdateDTO;
import com.EduTech.dto.qna.QuestionWriteDTO;
import com.EduTech.dto.qna.SearchDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.qna.Answer;
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
	public PageResponseDTO<QuestionDTO> QnAView(SearchDTO searchDTO) {
	    // 조건 및 페이지 객체 세팅
	    Specification<Question> spec = QnASpecs.searchQnA(
	            searchDTO.getType(),
	            searchDTO.getSearch(),
	            searchDTO.getSortBy(),
	            searchDTO.getSort(),
	            searchDTO.getStartDate(),
	            searchDTO.getEndDate()
	    );
	    Pageable pageable = PageRequest.of(searchDTO.getPageCount(), 10);

	    // 질문 페이지 조회
	    Page<Question> questionPage = questionRepository.findAll(spec, pageable);

	    // 질문 번호 리스트
	    List<Long> questionNums = questionPage.stream()
	            .map(Question::getQuestionNum)
	            .distinct()
	            .toList();

	    // 질문 번호에 해당하는 답변 조회 (DTO로 바로 조회)
	    List<AnswerDTO> answers = answerRepository.selectAnswer(questionNums);

	    // questionNum기준으로 그룹핑해 답변을 맵에 담음 
	    Map<Long, List<AnswerDTO>> answerMap = answers.stream()
	            .collect(Collectors.groupingBy(
	                    AnswerDTO::getQuestionNum
	            ));

	    // 질문 페이지를 DTO로 변환하면서 답변 연결
	    Page<QuestionDTO> dtoPage = questionPage.map(q -> {
	        QuestionDTO dto = new QuestionDTO();
	        dto.setTitle(q.getTitle());
	        dto.setContent(q.getContent());
	        dto.setState(q.getState());
	        dto.setCreatedAt(q.getCreatedAt());
	        dto.setUpdatedAt(q.getUpdatedAt());
	        dto.setView(q.getView());
	        dto.setMemId(q.getMember().getMemId());
	        dto.setQuestionNum(q.getQuestionNum());

	        // 답변 연결
	        dto.setAnswerList(answerMap.getOrDefault(q.getQuestionNum(), List.of()));
	        // List.of()로 null값 방지하며, questionNum을 키로 넣어 value값을 dtoPage에 넣음
	        return dto;
	    });

	    return new PageResponseDTO<>(dtoPage);
	}


	
	// 상세 페이지 질문 글 및 답변 조회
	public QuestionDTO QnAViewDetail(Long questionNum) {
		questionRepository.updateQuestion(questionNum); // 조회수 +1
		QuestionDTO dto = modelMapper.map(questionRepository.findById(questionNum), QuestionDTO.class); // 모델 매퍼로 질문 번호를 입력해 dto에 question객체 받아온 후
		dto.setAnswerList(answerRepository.selectAnswer(Collections.singletonList(questionNum))); // dto에 답변 받아온 후 리턴
		return dto;
		
	}
	
	//  질문 글 추가할때 사용하는 기능
	public void addQuestion(QuestionWriteDTO questionWriteDTO,String memId) {
		Question question=modelMapper.map(questionWriteDTO, Question.class);
		Member member = memberRepository.findById(memId)
			    .orElseThrow(() -> new EntityNotFoundException("해당 회원이 존재하지 않습니다."));
		question.setMember(member);
		 question.setView(0L);
		 question.setAnswer(new ArrayList<>());
		questionRepository.save(question);
	}
	
	// 질문 글 수정할때 사용하는 기능
	public void updateQuestion(QuestionUpdateDTO questionUpdateDTO,String memId) {
		// 글 작성 아이디 알기 위해 question 객체 받아옴
		 Question q = questionRepository.findById(questionUpdateDTO.getQuestionNum())
		            .orElseThrow(() -> new EntityNotFoundException("질문이 존재하지 않습니다."));

		    // 작성자 아니고 admin 아니면 권한 없음
		    if (!q.getMember().getMemId().equals(memId) && !"admin".equals(memId)) {
		        throw new AccessDeniedException("수정 권한이 없습니다.");
		    }

		questionRepository.updateQuestion(questionUpdateDTO.getTitle(),questionUpdateDTO.getContent(),questionUpdateDTO.getState(),questionUpdateDTO.getQuestionNum(),LocalDateTime.now());
	}
	
	//  질문 글 삭제할때 사용하는 기능
		public void deleteQuestions(List<Long> questionNums,String memId) {
			// 글 작성 아이디 알기 위해 question 객체 받아옴
			List<Question> questions = questionRepository.findAllById(questionNums);

		    // 작성자 아니고 admin 아니면 권한 없음
		    for (Question q : questions) {
		        if (!q.getMember().getMemId().equals(memId) && !"admin".equals(memId)) {
		            throw new AccessDeniedException("삭제 권한이 없습니다.");
		        }
		    }
			questionRepository.deleteAllById(questionNums);
		}
}
