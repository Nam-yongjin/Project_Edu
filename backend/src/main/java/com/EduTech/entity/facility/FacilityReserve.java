package com.EduTech.entity.facility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.EduTech.entity.member.Member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facility_reserve")
@Getter
@Setter
@NoArgsConstructor
public class FacilityReserve {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reserveId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fac_rev_num", nullable = false)
    private Facility facility;

    @Column(nullable = false)
    private LocalDate facDate; // 예약일

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    @Column(nullable = false)
    private LocalDateTime reserveAt; // 신청일

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private FacilityState state = FacilityState.WAITING;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "mem_id", nullable = true)
    private Member member;

    @PrePersist
    void onCreate() {
        if (reserveAt == null) reserveAt = LocalDateTime.now();
    }
}