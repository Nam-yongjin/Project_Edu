package com.EduTech.entity.facility;

import java.time.LocalDate;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "public_holiday", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"the_date"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicHoliday {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "the_date", nullable = false)
    private LocalDate date;        // 공휴일 날짜

    @Column(nullable = false, length = 100)
    private String name;           // 휴일명 (ex. 설날)

    @Column(nullable = false)
    private boolean isLunar;       // 음력 기반 여부(참고용, KASI 응답엔 직접값 없어서 false로 둬도 무방)
}
