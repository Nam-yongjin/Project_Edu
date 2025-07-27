package com.EduTech.entity.facility;

import java.time.LocalDate;
import java.time.LocalTime;

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
@Table(name = "facility_time")
@Getter
@Setter
public class FacilityTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long facTimeNum;

    private LocalDate facDate;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean available; // 예약 가능 여부

    @ManyToOne
    @JoinColumn(name = "facility_num")
    private Facility facility;
}
