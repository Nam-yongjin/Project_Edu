package com.EduTech.entity.facility;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "facility_image")
@Getter
@Setter
public class FacilityImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facImageNum;		// 이미지 번호

    private String imageName;		// 이미지 이름

    private String imageUrl;		// 이미지 URL

    @ManyToOne
    @JoinColumn(name = "facility_num")
    private Facility facility;		// 장소번호
}