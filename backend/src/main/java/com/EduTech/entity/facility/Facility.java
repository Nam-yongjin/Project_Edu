package com.EduTech.entity.facility;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    private Long facilityNum;		// 장소번호

    @Column(length = 100)
    private String facName;			// 장소명

    private String facInfo;			// 소개

    private int capacity;			// 수용인원

    private String facItem;			// 구비품목

    private String etc;				// 기타유의사항

    // 관계 설정 생략 가능 (양방향 필요 시 추가)
}