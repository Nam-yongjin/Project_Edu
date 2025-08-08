package com.EduTech.entity.event;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EventBanner {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long bannerNum;
	
	@Column(nullable = false, length = 100)
	private String originalName;
	
	@Column(nullable = false, length = 200)
	private String filePath;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "eventNum", nullable = false)
	private EventInfo eventInfo;
}