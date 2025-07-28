package com.EduTech.service.Event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;

import com.EduTech.dto.event.EventBannerDTO;
import com.EduTech.entity.event.EventBanner;
import com.EduTech.entity.event.EventCategory;
import com.EduTech.entity.event.EventInfo;
import com.EduTech.entity.event.EventState;
import com.EduTech.repository.event.EventBannerRepository;
import com.EduTech.repository.event.EventInfoRepository;
import com.EduTech.service.event.EventService;
import com.EduTech.util.FileUtil;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
@Rollback(false)
public class EventServiceTest5 {

    @Autowired
    private EventService eventService;

    @Autowired
    private EventInfoRepository eventInfoRepository;

    @Autowired
    private EventBannerRepository eventBannerRepository;

    @Autowired
    private FileUtil fileUtil;

    private EventInfo createTestEvent(String name) {
        return eventInfoRepository.save(EventInfo.builder()
                .eventName(name)
                .eventInfo("테스트 행사 설명")
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(1))
                .eventStartPeriod(LocalDateTime.now().plusDays(2))
                .eventEndPeriod(LocalDateTime.now().plusDays(10))
                .place("강의실 A")
                .daysOfWeek(List.of(1, 3, 5))
                .category(EventCategory.USER)
                .maxCapacity(30)
                .currCapacity(0)
                .state(EventState.OPEN)
                .build());
    }
 /*   
    private MockMultipartFile createValidMockImageFile() throws Exception {
        // 1. 임시 이미지 생성
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        image.getGraphics().setColor(Color.BLUE);
        image.getGraphics().fillRect(0, 0, 100, 100);

        // 2. 파일로 저장
        File tempFile = File.createTempFile("temp", ".jpg");
        ImageIO.write(image, "jpg", tempFile);

        // 3. MockMultipartFile로 읽기
        try (FileInputStream fis = new FileInputStream(tempFile)) {
            return new MockMultipartFile("file", "test.jpg", "image/jpeg", fis);
        } finally {
            tempFile.delete(); // 테스트 후 파일 삭제
        }
    }
*/
    @Test
    @DisplayName("1. 배너 등록 테스트")
    public void testRegisterBanner() throws Exception {
    	 try {
        // Given
        EventInfo event = createTestEvent("배너 행사");
        MockMultipartFile mockFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "fake-image-data".getBytes());
        EventBannerDTO dto = new EventBannerDTO();
        dto.setEventInfoId(event.getEventNum());

        // When
        eventService.registerBanner(dto, mockFile);

        // Then
        List<EventBanner> all = eventBannerRepository.findAll();
        assertEquals(1, all.size());
        assertEquals(event.getEventNum(), all.get(0).getEventInfo().getEventNum());
        System.out.println(" 등록된 배너 파일 경로: " + all.get(0).getFilePath());
    	 } catch (Exception e) {
    	        e.printStackTrace();
    	        fail("예외 발생: " + e.getMessage());
    	    }
    }

    @Test
    @DisplayName("2. 배너 삭제 테스트")
    public void testDeleteBanner() {
        // Given
        EventInfo event = createTestEvent("삭제 행사");
        EventBanner banner = eventBannerRepository.save(EventBanner.builder()
                .eventInfo(event)
                .originalName("image.png")
                .filePath("/Event/banner/image.png")
                .build());

        Long bannerId = banner.getEvtFileNum();

        // When
        eventService.deleteBanner(bannerId);

        // Then
        boolean exists = eventBannerRepository.existsById(bannerId);
        assertFalse(exists, "배너 삭제 실패");
        System.out.println(" 배너가 성공적으로 삭제되었습니다.");
    }

    @Test
    @DisplayName("3. 유효한 배너 목록 조회 테스트")
    public void testGetAllBanners() {
        // Given
        EventInfo event = createTestEvent("조회 행사");
        eventBannerRepository.save(EventBanner.builder()
                .eventInfo(event)
                .originalName("banner.jpg")
                .filePath("/Event/banner/banner.jpg")
                .build());

        // When
        List<EventBannerDTO> banners = eventService.getAllBanners();

        // Then
        assertFalse(banners.isEmpty(), "배너 목록이 비어 있습니다.");
        assertEquals("조회 행사", banners.get(0).getEventName());
        System.out.println(" 조회된 배너: " + banners.get(0).getOriginalName());
    }
}
