package com.EduTech.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.EduTech.dto.demonstration.DemonstrationFormResDTO;
import com.EduTech.dto.event.EventInfoDTO;
import com.EduTech.dto.facility.FacilityListDTO;
import com.EduTech.dto.facility.FacilityRegisterDTO;
import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.facility.Facility;
import com.EduTech.entity.facility.FacilityImage;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
            .setFieldMatchingEnabled(true)
            .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
            .setMatchingStrategy(MatchingStrategies.LOOSE)
            .setAmbiguityIgnored(true)
            .setSkipNullEnabled(true);

        // EventInfoDTO -> EventInfo (기존)
        modelMapper.typeMap(EventInfoDTO.class, EventInfo.class)
            .addMappings(mapper -> {
                mapper.skip(EventInfo::setEventNum);
                mapper.skip(EventInfo::setApplyAt);
                mapper.skip(EventInfo::setMainImagePath);
                mapper.skip(EventInfo::setFilePath);
                mapper.skip(EventInfo::setOriginalName);
            });

        // FacilityRegisterDTO -> Facility (기존)
        modelMapper.typeMap(FacilityRegisterDTO.class, Facility.class)
            .addMappings(mapper -> {
                mapper.skip(Facility::setFacRevNum);
            });

        // Demonstration -> DemonstrationFormResDTO (기존)
        modelMapper.typeMap(Demonstration.class, DemonstrationFormResDTO.class)
            .addMappings(mapper -> {
                mapper.skip(DemonstrationFormResDTO::setImageUrlList);
                mapper.skip(DemonstrationFormResDTO::setImageNameList);
                mapper.skip(DemonstrationFormResDTO::setIsMain);
            });

        // Facility -> FacilityListDTO 
        // images 정렬 규칙: mainImage = true 먼저, 다음 facImageNum 오름차순
        Converter<List<FacilityImage>, List<String>> imagesConverter = ctx -> {
            List<FacilityImage> src = ctx.getSource();
            if (src == null || src.isEmpty()) return List.of();
            return src.stream()
                .filter(Objects::nonNull)
                .sorted(
                    Comparator
                        .comparing((FacilityImage img) -> Boolean.TRUE.equals(img.getMainImage()) ? 0 : 1)
                        .thenComparing(FacilityImage::getFacImageNum, Comparator.nullsLast(Long::compareTo))
                )
                .map(FacilityImage::getImageUrl)
                .filter(Objects::nonNull)
                .toList();
        };

        modelMapper.typeMap(Facility.class, FacilityListDTO.class)
            .addMappings(mapper -> {
                mapper.map(Facility::getFacRevNum, FacilityListDTO::setFacRevNum);
                mapper.map(Facility::getFacName,   FacilityListDTO::setFacName);
                mapper.map(Facility::getFacInfo,   FacilityListDTO::setFacInfo);
                mapper.map(Facility::getCapacity,  FacilityListDTO::setCapacity);
                mapper.using(imagesConverter).map(Facility::getImages, FacilityListDTO::setImages);
            });

        return modelMapper;
    }
}
