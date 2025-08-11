package com.EduTech.entity.facility;

import java.time.LocalTime;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facility")
@Getter @Setter
@NoArgsConstructor
public class Facility { // 장소 정보

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facRevNum;		// 장소 번호

    @Column(nullable = false, length = 50)
    private String facName;		// 장소명

    @Column(nullable = false, columnDefinition = "TEXT")
    private String facInfo;		// 장소 소개

    @Column(nullable = false)
    private Integer capacity;	// 수용인원

    @Column(nullable = false, length = 255)
    private String facItem;		// 구비품목

    @Column(columnDefinition = "TEXT")
    private String etc;			// 유의사항
    
    @Column(name = "reserve_start")
    private LocalTime reserveStart;
    
    @Column(name = "reserve_end")
    private LocalTime reserveEnd;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityReserve> reserves = new ArrayList<>();


    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityHoliday> holidays = new ArrayList<>();

    
    // ---- 연관관계 편의 메서드 ----
    public void addImage(FacilityImage img) {
        images.add(img);
        img.setFacility(this); // 한쪽만 add, 중복 방지
    }

    public void removeImage(FacilityImage img) {
        images.remove(img);
        img.setFacility(null);
    }
}