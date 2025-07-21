package com.EduTech.service.member;

import com.EduTech.dto.member.CompanyRegisterDTO;
import com.EduTech.dto.member.MemberRegisterDTO;
import com.EduTech.dto.member.StudentRegisterDTO;
import com.EduTech.dto.member.TeacherRegisterDTO;

public interface MemberService {

	void registerMember(MemberRegisterDTO memberRegisterDTO);
	
	void registerStudent(StudentRegisterDTO studentRegisterDTO);
	
	void registerTeacher(TeacherRegisterDTO teacherRegisterDTO);
	
	void registerCompany(CompanyRegisterDTO companyRegisterDTO);
	
	boolean isDuplicatedId(String memId);
}