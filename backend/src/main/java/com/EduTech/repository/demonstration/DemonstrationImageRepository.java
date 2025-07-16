package com.EduTech.repository.demonstration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.dto.demonstration.DemonstrationImageDTO;
import com.EduTech.entity.demonstration.DemonstrationImage;

public interface DemonstrationImageRepository extends JpaRepository<DemonstrationImage, Long>{ // 실증 상품 이미지 관련 페포지토리
	
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationImageDTO(di.imageName, di.imageUrl) FROM demonstration_image di WHERE di.demNum=:demNum") // 리포지토리에서 생성자 호출해서 객체를 리스트에 담음.
	List<DemonstrationImageDTO> selectDemImage(Long demNum); // 실증 상품 번호를 가져와 해당 하는 이미지들을 리스트 형태로 받음.
}
