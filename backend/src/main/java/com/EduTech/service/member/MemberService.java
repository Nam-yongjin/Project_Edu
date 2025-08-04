package com.EduTech.service.member;

import com.EduTech.dto.member.CompanyDetailDTO;
import com.EduTech.dto.member.CompanyModifyDTO;
import com.EduTech.dto.member.CompanyRegisterDTO;
import com.EduTech.dto.member.MemberDTO;
import com.EduTech.dto.member.MemberDetailDTO;
import com.EduTech.dto.member.MemberModifyDTO;
import com.EduTech.dto.member.MemberRegisterDTO;
import com.EduTech.dto.member.MemberResetPwDTO;
import com.EduTech.dto.member.StudentDetailDTO;
import com.EduTech.dto.member.StudentModifyDTO;
import com.EduTech.dto.member.StudentRegisterDTO;
import com.EduTech.dto.member.TeacherDetailDTO;
import com.EduTech.dto.member.TeacherModifyDTO;
import com.EduTech.dto.member.TeacherRegisterDTO;
import com.EduTech.entity.member.Member;

public interface MemberService {

	void registerMember(MemberRegisterDTO memberRegisterDTO);
	
	void registerStudent(StudentRegisterDTO studentRegisterDTO);
	
	void registerTeacher(TeacherRegisterDTO teacherRegisterDTO);
	
	void registerCompany(CompanyRegisterDTO companyRegisterDTO);
	
	boolean isDuplicatedId(String memId);
	
	MemberDetailDTO readMemberInfo(String memId);
	
	StudentDetailDTO readStudentInfo(String memId);
	
	TeacherDetailDTO readTeacherInfo(String memId);
	
	CompanyDetailDTO readCompanyInfo(String memId);
	
	void modifyMemberInfo(String memId, MemberModifyDTO memberModifyDTO);
	
	void modifyStudentInfo(String memId, StudentModifyDTO studentModifyDTO);
	
	void modifyTeacherInfo(String memId, TeacherModifyDTO teacherModifyDTO);
	
	void modifyCompanyInfo(String memId, CompanyModifyDTO companyModifyDTO);
	
	void leaveMember(String memId);
	
	String findId(String phone);
	
	public boolean checkIdPhone(MemberResetPwDTO memberResetPwDTO);
	
	void resetPw(String memId, MemberResetPwDTO memberResetPwDTO);
	
	MemberDTO getKakaoMember(String accessToken);
	
	MemberDTO getNaverMember(String code, String state);

	default MemberDTO entityToDTO(Member member) {

		return new MemberDTO(member.getMemId(), member.getPw(), member.getEmail(), member.getState().name(), member.getRole().name());
	}

}