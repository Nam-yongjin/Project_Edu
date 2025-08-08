package com.EduTech.repository.demonstration;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EduTech.entity.demonstration.DemonstrationRequest;

public interface DemonstrationRequestRepository extends JpaRepository<DemonstrationRequest, Long>{
	
}
