package com.EduTech.entity.facility;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "facility_holiday")
@Getter
@Setter
public class FacilityHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate holidayDate;

    private String reason; // 예: 정기휴무, 공휴일 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_num")
    private Facility facility;
}