package com.EduTech.entity.demonstration;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "demonstration_time")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DemonstrationTime {

	@Id
	@Column(nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long demTimeNum; // 시간번호

	@Column(nullable = false)
	private LocalDate demDate; // 날짜

	@Column(nullable = false)
	private Boolean state; // 예약상태

	@ManyToOne
	@JoinColumn(name = "demNum")
	private Demonstration demonstration;
}
