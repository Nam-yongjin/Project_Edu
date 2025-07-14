package com.EduTech.entity.facility;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facility_image")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FacilityImage {

	@Id
	@Column(nullable = false) //이미지번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long facImageNum;
	
	@Column(nullable = false) //이미지이름
	private String imageName;
	
	@Column(nullable = false) //이미지URL
	private String imageUrl;
	
	@ManyToOne(fetch = FetchType.LAZY) //하나의 시설에 여러 개의 이미지 가능
	@JoinColumn(name = "facilityNum", nullable = false) //장소번호
	private Facility facility;
	
	
}
