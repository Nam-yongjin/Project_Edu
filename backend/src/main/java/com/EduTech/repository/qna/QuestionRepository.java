package com.EduTech.repository.qna;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.EduTech.dto.demonstration.DemonstrationPageListDTO;
import com.EduTech.entity.qna.Question;

public interface QuestionRepository extends JpaRepository<Question, Long>{
	// 문의 사항 조회를 담당하는 쿼리문
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationPageListDTO(demNum, demName, demMfr, itemNum) FROM Demonstration")
	Page<DemonstrationPageListDTO> selectPageDem(Pageable pageable);
}
