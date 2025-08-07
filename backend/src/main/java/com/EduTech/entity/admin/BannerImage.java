package com.EduTech.entity.admin;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "banner_image")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BannerImage {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bannerNum; // 이미지번호

	private String originalName; // 원본파일명
	
	private String imagePath; // 이미지저장경로
	
	private int sequence; // 배너순서
}
