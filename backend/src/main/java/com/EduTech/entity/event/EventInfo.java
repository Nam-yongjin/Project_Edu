package com.EduTech.entity.event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
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
public class EventInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventNum;

    @Column(nullable = false, length = 200)
    private String eventName;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String eventInfo;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime applyAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime applyStartPeriod;

    @Column(nullable = false)
    private LocalDateTime applyEndPeriod;

    @Enumerated(EnumType.STRING)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EventState state;

    @Column(nullable = false, length = 20)
    private String place;

    @Column(nullable = false)
    private LocalDateTime eventStartPeriod;

    @Column(nullable = false)
    private LocalDateTime eventEndPeriod;

    @Column(nullable = false)
    private int maxCapacity;

    @Column(name = "curr_capacity", nullable = false)
    private int currCapacity;

    @Column(length = 1000)
    private String etc;

    // 대표 첨부파일
    @Column(length = 100)
    private String originalName;

    @Column(length = 200)
    private String filePath;

    // 대표 이미지
    @Column(length = 200)
    private String mainImagePath;

    @Column(length = 100)
    private String mainImageOriginalName;
    

    // 다중 첨부파일
    @OneToMany(mappedBy = "eventInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventFile> attachList = new ArrayList<>();

    // 다중 이미지
    @OneToMany(mappedBy = "eventInfo", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EventImage> imageList = new ArrayList<>();

    @PrePersist
    public void prePersist() {
        if (this.state == null) {
            this.state = EventState.BEFORE;
        }
    }
    
    public void increaseCurrCapacity() {
        this.currCapacity += 1;
    }
}