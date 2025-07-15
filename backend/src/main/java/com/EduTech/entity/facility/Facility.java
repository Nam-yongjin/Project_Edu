package com.EduTech.entity.facility;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "facility")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Facility {

	@Id
	@Column(nullable = false) //장소번호
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long facilityNum;
	
	@Column(nullable = false) //장소명
	private String facName;
	
	@Column(nullable = false) //소개
	private String facInfo;
	
	@Enumerated(EnumType.STRING) //분류
	@Column(nullable = false)
	private FacilityCategory category;
	
	@Column(nullable = false) //수용인원
	private int capacity;
	
	@Column(nullable = false) //구비품목
	private String facItem;
	
	@Column //기타유의사항
	private String etc;
	
	@OneToMany(mappedBy = "facility", cascade = CascadeType.ALL) //하나의 시설에 여러 개 이미지 가능, 시설 삭제되면 이미지도 같이 삭제
	private List<FacilityImage> facilityImage = new ArrayList<>(); //null방지 초기화	

	@OneToMany(mappedBy = "facility", cascade = CascadeType.ALL) //하나의 시설에 여러 명 예약 가능, 시설 삭제되면 신청도 같이 삭제
	private List<FacilityReserve> facilityReserve = new ArrayList<>();
	
	@OneToMany(mappedBy = "facility", cascade = CascadeType.ALL) //하나의 시설에 여러 시간 예약 가능, 시설 삭제되면 시간도 같이 삭제
	private List<FacilityTime> facilityTime = new ArrayList<>();
}
