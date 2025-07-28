package com.EduTech.entity.facility;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "facility")
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityNum;		// 장소번호.

    @Column(length = 100)
    private String facName;			// 장소명

    private String facInfo;			// 소개

    private int capacity;			// 수용인원

    @Column(nullable = true)
    private String facItem;			// 구비품목

    private String etc;				// 기타유의사항
    
    @Builder.Default
    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FacilityImage> images = new ArrayList<>();

    public void addImage(FacilityImage image) {
        images.add(image);
        image.setFacility(this);
    }
    
    public List<FacilityImage> getImages() {
        return images;
    }
}