package com.EduTech.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.member.Teacher;

public interface TeacherRepository  extends JpaRepository<Teacher, String>{

	@Query("SELECT t FROM Teacher t JOIN FETCH t.member WHERE t.memId = :memId")
	Optional<Teacher> findByIdWithMember(@Param("memId") String memId);
}
