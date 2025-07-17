package com.EduTech.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.member.Student;

public interface StudentRepository extends JpaRepository<Student, String>{

	@Query("SELECT s FROM Student s JOIN FETCH s.member WHERE s.memId = :memId")
	Optional<Student> findByIdWithMember(@Param("memId") String memId);
}
