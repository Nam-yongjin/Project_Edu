package com.EduTech.entity.facility;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "facility")
@Getter @Setter
@NoArgsConstructor
public class Facility { // 장소 정보

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facRevNum;

    @Column(nullable = false, length = 50)
    private String facName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String facInfo;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, length = 255)
    private String facItem;

    @Column(columnDefinition = "TEXT")
    private String etc;

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityReserve> reserves = new ArrayList<>();

    @OneToMany(mappedBy = "facility", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityTime> times = new ArrayList<>();

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