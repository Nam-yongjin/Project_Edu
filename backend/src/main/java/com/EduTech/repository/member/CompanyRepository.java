package com.EduTech.repository.member;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.member.Company;

public interface CompanyRepository  extends JpaRepository<Company, String>{

}
