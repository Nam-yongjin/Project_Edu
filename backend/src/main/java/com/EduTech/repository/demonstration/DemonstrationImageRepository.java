package com.EduTech.repository.demonstration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.demonstration.DemonstrationImageDTO;
import com.EduTech.entity.demonstration.DemonstrationImage;

public interface DemonstrationImageRepository extends JpaRepository<DemonstrationImage, Long> { // 실증 상품 이미지 관련 페포지토리

	// 실증 상품 번호들을 가져와 해당 하는 이미지들을 리스트 형태로 받음.
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationImageDTO(di.imageName, di.imageUrl,di.demonstration.demNum) FROM DemonstrationImage di WHERE di.demonstration.demNum IN :demNum")
	List<DemonstrationImageDTO> selectDemImageIn(@Param("demNum") List<Long> demNum); 
	
	// 실증 상품 번호를 가져와 해당 하는 이미지들을 리스트 형태로 받음.
	@Query("SELECT new com.EduTech.dto.demonstration.DemonstrationImageDTO(di.imageName, di.imageUrl,di.demonstration.demNum) FROM DemonstrationImage di WHERE di.demonstration.demNum=:demNum")
	List<DemonstrationImageDTO> selectDemImage(@Param("demNum") Long demNum); 
	
	// 실증 상품 수정 페이지에서 url만 받아오는 쿼리문
	@Query("SELECT di.imageUrl FROM DemonstrationImage di WHERE di.demonstration.demNum=:demNum")
	List<String> selectDemImageUrl(@Param("demNum") Long demNum); 
	
	// 실증 상품 번호를 받아서 해당하는 이미지를 전부 삭제하는 쿼리문
	@Modifying 
	@Transactional
    @Query("DELETE FROM DemonstrationImage WHERE demonstration.demNum=:demNum")
	void deleteDemNumImage(@Param("demNum") Long demNum);
}
