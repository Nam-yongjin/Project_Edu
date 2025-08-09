package com.EduTech.repository.demonstration;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.EduTech.entity.demonstration.DemonstrationRequest;

public interface DemonstrationRequestRepository extends JpaRepository<DemonstrationRequest, Long>{
	// 아이디들을 가져와서 기업 목록들을 가져오는 쿼리문
	@Query("SELECT r FROM DemonstrationRequest r WHERE r.reserve.demRevNum IN :demRevNums")
	List<DemonstrationRequest> findStateByDemRevNumIn(@Param("demRevNums") List<Long> demRevNums);
}
