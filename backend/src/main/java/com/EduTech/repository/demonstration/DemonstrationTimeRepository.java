package com.EduTech.repository.demonstration;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationTimeDTO;
import com.EduTech.entity.demonstration.DemonstrationTime;

public interface DemonstrationTimeRepository extends JpaRepository<DemonstrationTime, Long>{ // 실증 시간 레포지토리 (상세시간)
	// 이미지 삭제와 실증 예약 시간 삭제같은 경우는 고아 처리를해 실증 신청을 삭제하면 전부 삭제되게 구현해 놓았기 때문에 하지않았음.
	
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationTimeDTO(dt.demDate, dt.state,dt.demNum) FROM demonstration_time dt WHERE dt.demDate IN:demDate") // 리포지토리에서 생성자 호출해서 객체를 리스트에 담음.
	List<DemonstrationTimeDTO> selectDemTime(List<LocalDate> demDate); // startDate 랑 endDate사이의 날짜를 저장한 demDate를 가져와 in사용하여 받아옴.
	
}
