package com.EduTech.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.member.Teacher;

public interface TeacherRepository  extends JpaRepository<Teacher, String>{

}
