package com.EduTech.repository.qna;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.qna.AnswerDTO;
import com.EduTech.entity.qna.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long>{
	
	// 답변 글 수정하는 쿼리문
	@Modifying
	@Transactional
	@Query("UPDATE Answer SET content=:content WHERE answerNum=:answerNum")
	void updateAnswer(@Param("content") String content, @Param("answerNum") Long answerNum);	
	
	// 답변 글 조회하는 쿼리문
	@Query("SELECT new com.EduTech.dto.qna.AnswerDTO(a.createdAt, a.content, a.answerNum) FROM Answer a WHERE a.question.questionNum =:questionNum")
	List<AnswerDTO> selectAnswer(@Param("questionNum") Long questionNum);
}
