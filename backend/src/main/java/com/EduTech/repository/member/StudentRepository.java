package com.EduTech.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.member.Student;

public interface StudentRepository extends JpaRepository<Student, String>{

}
