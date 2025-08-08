package com.EduTech.entity.facility;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "facility_holiday")
@Getter
@Setter
@NoArgsConstructor
public class FacilityHoliday {
	
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="fac_rev_num", nullable=false)
    private Facility facility;

    private LocalDate holidayDate;

    @Enumerated(EnumType.STRING)
    private HolidayReason reason;
    
}
