package com.EduTech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.time.LocalDateTime;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.jupiter.api.BeforeEach;
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

import jakarta.transaction.Transactional;

@Transactional
@Rollback(false)
@SpringBootTest
public class EventServiceTestBanner {

    @Autowired
    private EventService eventService;
    
    @Autowired
    private EventInfoRepository eventInfoRepository;
    
    @Autowired
    private EventBannerRepository bannerRepository;

    private EventInfo createTestEvent(String name) {
        return eventInfoRepository.save(EventInfo.builder()
                .eventName(name)
                .eventInfo("테스트 행사 설명1")
                .applyStartPeriod(LocalDateTime.now().minusDays(1))
                .applyEndPeriod(LocalDateTime.now().plusDays(1))
                .eventStartPeriod(LocalDateTime.now().plusDays(2))
                .eventEndPeriod(LocalDateTime.now().plusDays(10))
                .place("강의실 A1")
                .daysOfWeek(List.of(1, 3, 5))
                .category(EventCategory.USER)
                .maxCapacity(30)
                .currCapacity(0)
                .state(EventState.OPEN)
                .build());
    }

    private MockMultipartFile createMockImageFile(String filename) throws Exception {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, 100, 100);
        g2d.dispose();

        File tempFile = File.createTempFile("temp", ".jpg");
        ImageIO.write(image, "jpg", tempFile);

        try (FileInputStream fis = new FileInputStream(tempFile)) {
            return new MockMultipartFile("file", filename, "image/jpeg", fis);
        } finally {
            tempFile.delete();
        }
    }
/*    
    @BeforeEach
    void setUp() {
        bannerRepository.deleteAll(); // 테스트 간 독립성 보장
        // 추가 초기화 작업
    }
*/
    @Test
    @DisplayName("1. 배너 등록 테스트")
    public void testRegisterBanner() throws Exception {
        try {
            // Given
            EventInfo event = createTestEvent("배너 행사1");
            MockMultipartFile mockFile = createMockImageFile("test1.jpg");

            EventBannerDTO dto = new EventBannerDTO();
            dto.setEventInfoId(event.getEventNum());

            // When
            eventService.registerBanner(dto, mockFile);

            // Then
            List<EventBanner> all = bannerRepository.findAll();
            assertEquals(1, all.size(), "배너가 정상적으로 저장되지 않았습니다.");
            assertEquals(event.getEventNum(), all.get(0).getEventInfo().getEventNum());

            System.out.println("등록된 배너 파일 경로: " + all.get(0).getFilePath());
        } catch (Exception e) {
            e.printStackTrace();
            fail("예외 발생: " + e.getMessage());
        }
    }

    //@Test
    @DisplayName("2. 배너 삭제 테스트")
    public void testDeleteBanner() throws Exception {
        try {
            // Given
            EventInfo event = createTestEvent("삭제 행사");
            MockMultipartFile file = createMockImageFile("delete.jpg");

            EventBannerDTO dto = new EventBannerDTO();
            dto.setEventInfoId(event.getEventNum());
            eventService.registerBanner(dto, file);

            Long evtFileNum = bannerRepository.findAll().get(0).getEvtFileNum();

            // When
            eventService.deleteBanner(evtFileNum);

            // Then
            boolean exists = bannerRepository.existsById(evtFileNum);
            assertFalse(exists, "배너 삭제 실패");
            System.out.println("배너가 성공적으로 삭제되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            fail("❌ 예외 발생: " + e.getMessage());
        }
    }

    //@Test
    @DisplayName("3. 유효한 배너 목록 조회 테스트")
    public void testGetAllBanners() throws Exception {
        try {
            // Given
            EventInfo event = createTestEvent("조회 행사");
            MockMultipartFile file = createMockImageFile("banner.jpg");

            EventBannerDTO dto = new EventBannerDTO();
            dto.setEventInfoId(event.getEventNum());
            eventService.registerBanner(dto, file);

            // When
            List<EventBannerDTO> banners = eventService.getAllBanners();

            // Then
            assertFalse(banners.isEmpty(), "배너 목록이 비어 있습니다.");
            assertEquals("조회 행사", banners.get(0).getEventName());
            System.out.println("조회된 배너 originalName: " + banners.get(0).getOriginalName());
            System.out.println("조회된 배너 filePath: " + banners.get(0).getFilePath());
        } catch (Exception e) {
            e.printStackTrace();
            fail("예외 발생: " + e.getMessage());
        }
    }
    
    @Test
    @DisplayName("3. 유효한 배너 목록 조회 테스트")
    public void testGetAllBanners2() throws Exception {
        try {
            // Given
            EventInfo event = createTestEvent("조회 행사");
            MockMultipartFile file = createMockImageFile("banner.jpg");

            EventBannerDTO dto = new EventBannerDTO();
            dto.setEventInfoId(event.getEventNum());
            eventService.registerBanner(dto, file);

            // When
            List<EventBannerDTO> banners = eventService.getAllBanners().stream()
                    .filter(b -> b.getEventName().equals("조회 행사"))
                    .toList();

            // Then
            assertFalse(banners.isEmpty(), "조회 행사에 대한 배너가 없습니다.");
            assertEquals("조회 행사", banners.get(0).getEventName());
            System.out.println("조회된 배너 originalName: " + banners.get(0).getOriginalName());
            System.out.println("조회된 배너 filePath: " + banners.get(0).getFilePath());
        } catch (Exception e) {
            e.printStackTrace();
            fail("예외 발생: " + e.getMessage());
        }
    }
}
