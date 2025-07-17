package com.EduTech.repository.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.member.Company;

public interface CompanyRepository  extends JpaRepository<Company, String>{

	@Query("SELECT c FROM Company c JOIN FETCH c.member WHERE c.memId = :memId")
	Optional<Company> findByIdWithMember(@Param("memId") String memId);
}
