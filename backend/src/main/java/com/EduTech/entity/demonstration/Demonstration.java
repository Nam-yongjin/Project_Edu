package com.EduTech.entity.demonstration;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "demonstration")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Demonstration { //  엔티티에 유효성 검사는 db에 데이터 삽입 시점에 검사하므로 프론트 쪽에서 1차적으로 유효성 검사가 한번 더 필요하다.

	@Id // 기본키 생성
	@Column(nullable = false) // 널값 방지
	@GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터 insert시 1씩 증가하게 설정 identity는 maria db랑 sql에서만 이용
	private Long demNum; // 실증 번호
	
	@Column(nullable = false)
	@NotBlank // 공백 허용 x
	@Size(max=100)  // 최대 글자수 100개까지, 한글과 영어 둘 다 동일하게 한글자씩 취급
	private String demName; // 물품명
	
	@Column(nullable = false)
	@Size(max=500)
	private String demInfo; // 물품소개
	
	@Column(nullable = false)
	@Size(max=100)
	private String demMfr; // 제조사
	
	@Column(nullable = false)
	@Min(0) // 최소값을 0으로 지정해서 음수값 방지 (null값은 방지x)
	private long itemNum; // 개수
	
	 @OneToMany(mappedBy = "demonstration", cascade = CascadeType.ALL, orphanRemoval = true) // cascade.All: 저장하면 이미지도 저장, 삭제하면 이미도 삭제됨.
	 																						// orpahnRemoval: demonstrate에서 이미지 리스트 하나를 빼면 그 이미지를 db에서 제거함
	 private List<DemonstrationImage> demonstrationImage = new ArrayList<>();
	 
	 
	 @OneToMany(mappedBy = "demonstration", cascade = CascadeType.ALL, orphanRemoval = true)  // 양방향을 위해 구현한것
	 private List<DemonstrationReserve> demonstrationReserve = new ArrayList<>();
	 
	 @OneToMany(mappedBy = "demonstration", cascade = CascadeType.ALL, orphanRemoval = true) 
	 private List<DemonstrationRegistration> demonstrationRegistration = new ArrayList<>();
	 
	 @OneToMany(mappedBy = "demonstration", cascade = CascadeType.ALL, orphanRemoval = true) 
	 private List<DemonstrationTime> demonstrationTime = new ArrayList<>();
	

}
