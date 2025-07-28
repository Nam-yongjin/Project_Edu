package com.EduTech.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import com.EduTech.entity.facility.Facility;
import com.EduTech.entity.facility.FacilityImage;
import com.EduTech.repository.facility.FacilityImageRepository;
import com.EduTech.repository.facility.FacilityRepository;

@DisplayName("FacilityRepository 테스트")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(false) // DB에 실제로 저장되도록 설정
@Transactional // 명시적으로 트랜잭션 관리
class FacilityRepositoryTest {

    @Autowired
    private FacilityRepository facilityRepository;

    @Autowired
    private FacilityImageRepository facilityImageRepository;

    //@Test
    @DisplayName("시설 저장 및 조회 테스트")
    void testSaveAndFindFacility() {
        System.out.println("=== 시설 저장 및 조회 테스트 시작 ===");

        Facility facility = Facility.builder()
                .facName("청소년문화센터")
                .facInfo("다목적 강당")
                .capacity(100)
                .facItem("책상, 의자, 마이크")
                .etc("예약 필수")
                .build();
        facilityRepository.save(facility);
        System.out.println(">>> 시설 저장 완료");

        Optional<Facility> result = facilityRepository.findByFacName("청소년문화센터");
        System.out.println(">>> 시설 조회 완료");

        assertThat(result).isPresent();
        assertThat(result.get().getFacInfo()).isEqualTo("다목적 강당");

        System.out.println("=== 시설 저장 및 조회 테스트 완료 ===");
    }


    //@Test
    @DisplayName("이미지 포함 시설 조회 테스트 (Fetch Join)")
    void testFindFacilityWithImages() {
        System.out.println("=== 이미지 포함 시설 조회 테스트 시작 ===");

        Facility facility = Facility.builder()
                .facName("포토존센터")
                .facInfo("사진 촬영 공간")
                .facItem("조명, 배경천, 삼각대")
                .capacity(20)
                .etc("예약 필수")
                .build();

        facility.addImage(FacilityImage.builder().imageName("photo1.jpg").imageUrl("/uploads/photo1.jpg").build());
        facility.addImage(FacilityImage.builder().imageName("photo2.jpg").imageUrl("/uploads/photo2.jpg").build());

        facilityRepository.save(facility);
        System.out.println(">>> 시설 및 이미지 저장 완료");

        Facility fetched = facilityRepository.findWithImagesByFacName("포토존센터").orElseThrow();
        System.out.println(">>> Fetch Join 조회 완료");

        assertThat(fetched.getImages()).hasSize(2);
        assertThat(fetched.getImages()).extracting(FacilityImage::getImageName)
                .containsExactlyInAnyOrder("photo1.jpg", "photo2.jpg");

        System.out.println("=== 이미지 포함 시설 조회 테스트 완료 ===");
    }
    
    //@Test
    @DisplayName("시설에 여러 이미지 저장 후 Fetch Join으로 조회")
    void testSaveFacilityWithMultipleImagesAndFetch() {
        System.out.println("=== 다중 이미지 시설 저장 및 조회 테스트 시작 ===");

        Facility facility = Facility.builder()
                .facName("복합문화공간")
                .facInfo("다목적 문화관")
                .facItem("마이크, 조명, 빔프로젝터")
                .capacity(80)
                .etc("예약 필수")
                .build();

        facility.addImage(createImage("img1.jpg", "/images/img1.jpg"));
        facility.addImage(createImage("img2.jpg", "/images/img2.jpg"));
        facility.addImage(createImage("img3.jpg", "/images/img3.jpg"));

        facilityRepository.save(facility);
        System.out.println(">>> 시설 및 이미지 3개 저장 완료");

        Facility result = facilityRepository.findWithImagesByFacName("복합문화공간").orElseThrow();
        System.out.println(">>> Fetch Join 조회 완료");

        assertThat(result.getImages()).hasSize(3);
        assertThat(result.getImages()).extracting(FacilityImage::getImageName)
                .containsExactlyInAnyOrder("img1.jpg", "img2.jpg", "img3.jpg");

        System.out.println("=== 다중 이미지 시설 저장 및 조회 테스트 완료 ===");
    }

    //@Test
    @DisplayName("시설 번호로 이미지 조회 테스트")
    void testFindImagesByFacilityNum() {
        System.out.println("=== 시설 번호 기반 이미지 조회 테스트 시작 ===");

        Facility facility = facilityRepository.save(Facility.builder()
                .facName("테스트관")
                .facInfo("테스트용")
                .capacity(50)
                .facItem("테스트용 장비")
                .build());

        facilityImageRepository.save(FacilityImage.builder()
                .facility(facility)
                .imageName("test.jpg")
                .imageUrl("/images/test.jpg")
                .build());

        System.out.println(">>> 시설 및 이미지 저장 완료");

        List<FacilityImage> images = facilityImageRepository.findByFacility_FacilityNum(facility.getFacilityNum());
        System.out.println(">>> 이미지 조회 완료");

        assertThat(images).hasSize(1);
        assertThat(images.get(0).getImageName()).isEqualTo("test.jpg");

        System.out.println("=== 시설 번호 기반 이미지 조회 테스트 완료 ===");
    }
    
    //@Test
    @DisplayName("전체 시설 목록 조회 테스트")
    void testFindAllFacilities() {
        System.out.println("=== 전체 시설 목록 조회 테스트 시작 ===");

        facilityRepository.save(Facility.builder()
                .facName("시설1")
                .facInfo("정보1")
                .capacity(10)
                .facItem("항목1")
                .etc("기타1")
                .build());
        facilityRepository.save(Facility.builder()
                .facName("시설2")
                .facInfo("정보2")
                .capacity(20)
                .facItem("항목2")
                .etc("기타2")
                .build());

        System.out.println(">>> 시설 2개 저장 완료");

        List<Facility> facilities = facilityRepository.findAll();
        System.out.println(">>> 전체 시설 조회 완료");

        assertThat(facilities).isNotEmpty();
        assertThat(facilities.size()).isGreaterThanOrEqualTo(2);

        System.out.println("=== 전체 시설 목록 조회 테스트 완료 ===");
    }
    
    //@Test
    @DisplayName("시설 정보 수정 테스트")
    void testUpdateFacilityInfo() {
        System.out.println("=== 시설 수정 테스트 시작 ===");

        Facility facility = facilityRepository.save(Facility.builder()
                .facName("수정전센터")
                .facInfo("기존 정보")
                .capacity(30)
                .facItem("기존 항목")
                .etc("기존 기타")
                .build());

        facility.setFacInfo("수정된 정보");
        facility.setFacItem("수정된 항목");
        facilityRepository.save(facility);
        System.out.println(">>> 시설 정보 수정 완료");

        Facility updated = facilityRepository.findById(facility.getFacilityNum()).orElseThrow();

        assertThat(updated.getFacInfo()).isEqualTo("수정된 정보");
        assertThat(updated.getFacItem()).isEqualTo("수정된 항목");

        System.out.println("=== 시설 수정 테스트 완료 ===");
    }
    
    //@Test
    @DisplayName("시설 삭제 시 이미지도 함께 삭제되는지 테스트")
    void testDeleteFacilityAndCascadeImages() {
        System.out.println("=== 시설 삭제 및 이미지 Cascade 테스트 시작 ===");

        Facility facility = Facility.builder()
                .facName("삭제센터")
                .facInfo("삭제용")
                .capacity(40)
                .facItem("삭제항목")
                .etc("삭제기타")
                .build();

        facility.addImage(createImage("del1.jpg", "/images/del1.jpg"));
        facility.addImage(createImage("del2.jpg", "/images/del2.jpg"));
        facility = facilityRepository.save(facility);

        Long facId = facility.getFacilityNum();
        System.out.println(">>> 시설 및 이미지 저장 완료");

        facilityRepository.deleteById(facId);
        System.out.println(">>> 시설 삭제 완료");

        assertThat(facilityRepository.findById(facId)).isEmpty();
        List<FacilityImage> images = facilityImageRepository.findByFacility_FacilityNum(facId);
        assertThat(images).isEmpty();

        System.out.println("=== 시설 삭제 Cascade 테스트 완료 ===");
    }

    //@Test
    @DisplayName("존재하지 않는 시설명을 조회할 때 Optional.empty() 반환 확인")
    void testFindByNonExistingFacilityName() {
        System.out.println("=== 존재하지 않는 시설명 조회 테스트 시작 ===");

        Optional<Facility> result = facilityRepository.findByFacName("존재하지않는센터");
        System.out.println(">>> 조회 시도 완료");

        assertThat(result).isEmpty();

        System.out.println("=== 존재하지 않는 시설명 조회 테스트 완료 ===");
    }

    //@Test
    @DisplayName("이미지 없이 시설만 저장되는지 테스트")
    void testSaveFacilityWithoutImages() {
        System.out.println("=== 이미지 없이 시설 저장 테스트 시작 ===");

        Facility facility = Facility.builder()
                .facName("이미지없는센터")
                .facInfo("이미지 없음")
                .capacity(25)
                .facItem("없음")
                .etc("주의사항 없음")
                .build();

        Facility saved = facilityRepository.save(facility);
        System.out.println(">>> 시설 저장 완료");

        Facility found = facilityRepository.findById(saved.getFacilityNum()).orElseThrow();
        assertThat(found.getImages()).isEmpty();

        System.out.println("=== 이미지 없이 시설 저장 테스트 완료 ===");
    }

    //@Test
    @DisplayName("중복 시설 이름 저장 테스트")
    void testDuplicateFacilityName() {
        System.out.println("=== 중복 시설 이름 저장 테스트 시작 ===");

        String duplicateName = "중복센터";

        Facility f1 = Facility.builder()
                .facName(duplicateName)
                .facInfo("정보1")
                .capacity(10)
                .facItem("항목1")
                .etc("비고1")
                .build();

        Facility f2 = Facility.builder()
                .facName(duplicateName)
                .facInfo("정보2")
                .capacity(20)
                .facItem("항목2")
                .etc("비고2")
                .build();

        facilityRepository.save(f1);
        System.out.println(">>> 첫 번째 시설 저장 완료");

        facilityRepository.save(f2);
        System.out.println(">>> 두 번째 시설 저장 완료");

        List<Facility> facilities = facilityRepository.findAll();
        long count = facilities.stream()
                .filter(f -> f.getFacName().equals(duplicateName))
                .count();

        assertThat(count).isGreaterThanOrEqualTo(2);
        System.out.println("=== 중복 시설 이름 저장 테스트 완료 ===");
    }

    //@Test
    @DisplayName("이미지 저장 시 facility null이면 예외 발생 테스트")
    void testSaveImageWithNullFacility() {
        System.out.println("=== Facility 없는 이미지 저장 예외 테스트 시작 ===");

        FacilityImage image = FacilityImage.builder()
                .facility(null)
                .imageName("orphan.jpg")
                .imageUrl("/images/orphan.jpg")
                .build();

        try {
            facilityImageRepository.save(image);
            System.out.println(">>> 저장 시도 완료 (예외 없으면 실패)");
            assertThat(false).as("예외가 발생해야 합니다").isTrue(); // 실패 유도
        } catch (Exception e) {
            System.out.println(">>> 예외 발생 확인됨: " + e.getClass().getSimpleName());
            assertThat(e).isInstanceOf(Exception.class);
        }

        System.out.println("=== Facility 없는 이미지 저장 예외 테스트 완료 ===");
    }

    //@Test
    @DisplayName("이미지만 삭제 시 Facility는 유지되는지 테스트")
    void testDeleteImageButKeepFacility() {
        System.out.println("=== 이미지만 삭제 테스트 시작 ===");

        Facility facility = facilityRepository.save(Facility.builder()
                .facName("이미지삭제센터")
                .facInfo("삭제 테스트")
                .capacity(20)
                .facItem("테스트 항목")
                .build());

        FacilityImage image = FacilityImage.builder()
                .facility(facility)
                .imageName("will_delete.jpg")
                .imageUrl("/img/delete.jpg")
                .build();

        facilityImageRepository.save(image);
        System.out.println(">>> 시설 및 이미지 저장 완료");

        facilityImageRepository.delete(image);
        System.out.println(">>> 이미지 삭제 완료");

        Optional<Facility> result = facilityRepository.findById(facility.getFacilityNum());
        assertThat(result).isPresent();

        System.out.println("=== 이미지만 삭제 테스트 완료 ===");
    }

    //@Test
    @DisplayName("이미지 순서 보장 테스트 (List 순서 유지)")
    void testImageOrderIsPreserved() {
        System.out.println("=== 이미지 순서 테스트 시작 ===");

        Facility facility = Facility.builder()
                .facName("순서센터")
                .facInfo("순서확인")
                .capacity(30)
                .facItem("항목A")
                .build();

        facility.addImage(createImage("1.jpg", "/img/1.jpg"));
        facility.addImage(createImage("2.jpg", "/img/2.jpg"));
        facility.addImage(createImage("3.jpg", "/img/3.jpg"));

        facilityRepository.save(facility);
        System.out.println(">>> 이미지 3개 순서대로 저장 완료");

        Facility fetched = facilityRepository.findWithImagesByFacName("순서센터").orElseThrow();
        List<FacilityImage> images = fetched.getImages();

        assertThat(images).extracting(FacilityImage::getImageName)
                .containsExactly("1.jpg", "2.jpg", "3.jpg");

        System.out.println("=== 이미지 순서 테스트 완료 ===");
    }

    //@Test
    @DisplayName("시설명 300자 이상 입력 시 예외 발생 테스트")
    void testFacilityNameTooLong() {
        System.out.println("=== 시설명 과다 입력 테스트 시작 ===");

        String longName = "A".repeat(300);

        Facility facility = Facility.builder()
                .facName(longName)
                .facInfo("긴 이름 테스트")
                .capacity(10)
                .facItem("항목")
                .build();

        try {
            facilityRepository.save(facility);
            System.out.println(">>> 저장 시도 완료 (예외 없으면 실패)");
            assertThat(false).as("예외가 발생해야 합니다").isTrue();
        } catch (Exception e) {
            System.out.println(">>> 예외 발생 확인됨: " + e.getMessage());
            assertThat(e).isInstanceOf(Exception.class);
        }

        System.out.println("=== 시설명 과다 입력 테스트 완료 ===");
    }

    
    //=============================================================================================================================================================
    
    // 헬퍼 메서드
    private FacilityImage createImage(String name, String url) {
        return FacilityImage.builder()
                .imageName(name)
                .imageUrl(url)
                .build();
    }
    
}
