package com.EduTech.entity.facility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "facility")
@Getter
@Setter
public class Facility {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facilityNum;

    @Column(nullable = false, length = 50)
    private String facName;

    private String facInfo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FacilityCategory category; // 예: CONFERENCE, SEMINAR (enum으로 따로 분리 권장)

    private int capacity;

    private String facItem;

    private String etc;

    // 관계 설정 생략 가능 (양방향 필요 시 추가)
}