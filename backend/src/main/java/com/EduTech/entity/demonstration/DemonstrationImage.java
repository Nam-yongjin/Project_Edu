package com.EduTech.entity.demonstration;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "demonstration_image")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DemonstrationImage {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long demImageNum; // 이미지 번호
	
	@Column(nullable = false)
	private String imageName; // 이미지 이름 (파일 저장 시스템에서 이용될 이미지임)
	
	@Column(nullable = false)
	private String imageUrl; // 이미지 URL

	@ManyToOne
	@JoinColumn(name = "demNum") // joincolumn으로 외래키 설정 (Demonstration_image와 demonstration은 다 대 일 관계이다.) demonstration에도 mappedby 사용해서 양방향으로 구현할 것임.
	private Demonstration demonstration;

}
