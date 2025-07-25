package com.EduTech.repository.qna;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.entity.qna.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>,JpaSpecificationExecutor<Question>{	
	// 질문 글 수정하는 쿼리문
	@Modifying
	@Transactional
	@Query("UPDATE Question SET title=:title, content=:content, state=:state WHERE questionNum=:questionNum")
	void updateQuestion(@Param("title") String title,@Param("content") String content,@Param("state") Boolean state, @Param("questionNum") Long questionNum);	

	
}
