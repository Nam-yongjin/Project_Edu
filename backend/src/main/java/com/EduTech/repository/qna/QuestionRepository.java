package com.EduTech.repository.qna;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.qna.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>{

}
