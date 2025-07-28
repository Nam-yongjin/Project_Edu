package com.EduTech.entity.facility;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "facility_holiday",
    uniqueConstraints = @UniqueConstraint(columnNames = {"facility_num", "holiday_date"})
)
@Getter
@Setter
public class FacilityHoliday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long holidayId;			// 휴일 번호 
    
    @Column(nullable = false)
    private LocalDate holidayDate;	// 휴일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HolidayReason reason; 	// 예: 정기휴무, 공휴일 등

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "facility_num")
    private Facility facility;
}