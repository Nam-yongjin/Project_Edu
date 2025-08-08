package com.EduTech.entity.facility;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facility_image")
@Getter @Setter
@NoArgsConstructor
public class FacilityImage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facImageNum;

    @Column(nullable = false, length = 255)
    private String imageName;

    @Column(nullable = false, length = 500)
    private String imageUrl;

    private Boolean mainImage = false; // 대표 여부(선택)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_num", nullable = false) // ★ 변경
    private Facility facility;

    // 필요하면 생성자 추가
    public FacilityImage(String imageName, String imageUrl, boolean mainImage) {
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.mainImage = mainImage;
    }

    // ※ 양방향 동기화는 Facility 쪽 helper에서 처리하므로 여기서는 단순 세터만 유지
    public void setFacility(Facility facility) {
        this.facility = facility;
    }
}