package com.EduTech.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.demonstration.DemonstrationImageDTO;
import com.EduTech.dto.demonstration.DemonstrationListRegistrationDTO;
import com.EduTech.dto.demonstration.DemonstrationListReserveDTO;
import com.EduTech.dto.demonstration.DemonstrationTimeResDTO;
import com.EduTech.entity.demonstration.Demonstration;
import com.EduTech.entity.demonstration.DemonstrationImage;
import com.EduTech.entity.demonstration.DemonstrationRegistration;
import com.EduTech.entity.demonstration.DemonstrationReserve;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.demonstration.DemonstrationTime;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.repository.demonstration.DemonstrationImageRepository;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.demonstration.DemonstrationTimeRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.util.FileUtil;

import jakarta.transaction.Transactional;

@SpringBootTest
public class DemonstrationRepositoryTests {
	@Autowired
	private DemonstrationRepository demonstrationRepository;

	@Autowired
	private DemonstrationImageRepository demonstrationImageRepository;

	@Autowired
	private DemonstrationRegistrationRepository demonstrationRegistrationRepository;

	@Autowired
	private DemonstrationReserveRepository demonstrationReserveRepository;

	@Autowired
	private DemonstrationTimeRepository demonstrationTimeRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private MemberRepository memberRepository;

	@MockBean
	private NoticeFileRepository noticeFileRepository;

	@MockBean
	MemberRepositoryTests memberRepositoryTests;
	@Autowired
	private FileUtil fileutil;

	// @Test
	public void testDemonstration() {
		for (int i = 0; i < 10; i++) {
			Demonstration demonstration = Demonstration.builder().demName("product" + i).demInfo("설명" + i)
					.demMfr("제조사다.").itemNum(Long.valueOf(i + 1)).build();

			demonstrationRepository.save(demonstration);
		}
	}

	public Demonstration testDemon() {
		Demonstration demonstration = Demonstration.builder().demName("product").demInfo("프로젝트 힘들다")
				.demMfr("제조사가 아닙니다.").itemNum(Long.valueOf(1)).build();

		return demonstrationRepository.save(demonstration);
	}

	public Member testMember() {
		Member member = Member.builder().memId("user").pw(passwordEncoder.encode("a")).name("USER")
				.email("aaa@test.com").birthDate(LocalDate.of(2025, 7, 15)).gender(MemberGender.MALE).phone("0101234321") // MALE 로 할것
				.addr("테스트주소").checkSms(true).checkEmail(false).state(MemberState.NORMAL).role(MemberRole.USER).build();

		return memberRepository.save(member);
	}

	/*
	 * @Test public void testDemonstrationImage() {
	 * 
	 * Demonstration demonstration = testDemon(); for (int i = 0; i < 10; i++) {
	 * DemonstrationImage demonstrationImage =
	 * DemonstrationImage.builder().imageName("이미지 이름")
	 * .imageUrl("URL 입니다.").demonstration(demonstration).build();
	 * 
	 * demonstrationImageRepository.save(demonstrationImage); } }
	 */

	// @Test
	public void testDemonstrationRegistration() {

		Demonstration demonstration = testDemon();
		Member member = testMember();
		for (int i = 0; i < 10; i++) {
			DemonstrationRegistration demonstrationRegistory = DemonstrationRegistration.builder()
					.regDate(LocalDate.now()).expDate(LocalDate.now()).state(DemonstrationState.WAIT)
					.demonstration(demonstration).member(member).build();

			demonstrationRegistrationRepository.save(demonstrationRegistory);

		}
	}

	// @Test
	public void testDemonstrationReserve() {

		Demonstration demonstration = testDemon();
		Member member = testMember();
		for (int i = 0; i < 10; i++) {
			DemonstrationReserve demonstrationReserveRegistory = DemonstrationReserve.builder().applyAt(LocalDate.now())
					.startDate(LocalDate.now()).endDate(LocalDate.now()).state(DemonstrationState.WAIT)
					.demonstration(demonstration).member(member).build();

			demonstrationReserveRepository.save(demonstrationReserveRegistory);

		}
	}

	// @Test
	public void testDemonstrationTime() {

		Demonstration demonstration = testDemon();
		Member member = testMember();
		for (int i = 0; i < 10; i++) {
			DemonstrationTime demonstrationTimeRegistory = DemonstrationTime.builder().demDate(LocalDate.now())
					.state(true).demonstration(demonstration).build();

			demonstrationTimeRepository.save(demonstrationTimeRegistory);

		}
	}

	// -------------------------------------------------여기까지 엔티티 테스트

	// @Test
	public void testDemonstrationImageAdd() { // 이이지 파일 불러와서 List에 담아서 db에 저장하는 테스트 (정상)
		List<MultipartFile> files = new ArrayList<>();
		List<DemonstrationImage> saveImage = new ArrayList<>();
		Demonstration demonstration = testDemon();
		try { // 테스트에서 이미지 파일 불러오는 과정 (불러와서 list에 담음)
			Path imagePath = Path.of("C:\\duke.png");
			byte[] imageBytes = Files.readAllBytes(imagePath);

			MockMultipartFile mockFile = new MockMultipartFile("file", // form field name
					"duke.png", // 원래 파일명
					"image/png", // content type
					imageBytes // 실제 파일 내용 (byte 배열)
			);
			files.add(mockFile);
			files.add(mockFile);

		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Object> fileUrl = fileutil.saveFiles(files, "files");
		for (Object obj : fileUrl) {
			if (obj instanceof Map) {
				Map<String, String> map = (Map<String, String>) obj;
				// System.out.println("원본 이름: " + map.get("originalName"));
				// System.out.println("파일 경로: " + map.get("filePath"));
				DemonstrationImage demonstrationimage = new DemonstrationImage();
				demonstrationimage.setImageName(map.get("originalName"));
				demonstrationimage.setImageUrl(map.get("filePath"));
				demonstrationimage.setDemonstration(demonstration);
				saveImage.add(demonstrationimage);
			}
		}
		demonstrationImageRepository.saveAll(saveImage);

	}
/*
	// @Test 저장된 이미지의 이름, 경로명이 잘 저장되있는지 확인하는 테스트 (정상)
	public void testDemonstrationGetUrlTest() {
		List<DemonstrationImageDTO> saveImage = new ArrayList<>();
		saveImage = demonstrationImageRepository.selectDemImage(Long.valueOf(15));
		System.out.println(saveImage);
	}
*/
	// ---------------------------------------------------------파일 테스트

	 //@Test // 실증 기업 조회 전체 페이지 불러와서 잘 불러오는지 확인 (정상)
	public void demonstrationRegistrationListTest() {
		// 전체 페이지를 가져오기 위해서는 처음 페이지를 무조건 호출해야 한다.
		List<Page<DemonstrationListRegistrationDTO>> allData = new ArrayList<>();
		Page<DemonstrationListRegistrationDTO> firstPage = demonstrationRegistrationRepository
				.selectPageDemReg(PageRequest.of(0, 5, Sort.by("regDate").descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<DemonstrationListRegistrationDTO> totalPage = demonstrationRegistrationRepository
					.selectPageDemReg(PageRequest.of(i, 5, Sort.by("regDate").descending()));
			allData.add(totalPage);
		}

		for (int i = 0; i < totalPageCount; i++) {
			System.out.println(allData.get(i).getContent()); // 페이지 목록 출력 (페이지 접근시 get(i))
		}
	}

	// @Test // 실증 기업 조회 페이지 불러와서 잘 불러오는지 확인<검색어 가져와서> (정상)
	public void demonstrationRegistrationListSearchTest() {
		List<Page<DemonstrationListRegistrationDTO>> allData = new ArrayList<>();
		Page<DemonstrationListRegistrationDTO> firstPage = demonstrationRegistrationRepository
				.selectPageDemReg(PageRequest.of(0, 5, Sort.by("regDate").descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<DemonstrationListRegistrationDTO> totalPage = demonstrationRegistrationRepository
					.selectPageDemRegSearch(PageRequest.of(i, 5, Sort.by("regDate").descending()), "크");
			allData.add(totalPage);
		}

		for (int i = 0; i < totalPageCount; i++) {
			System.out.println(allData.get(i).getContent()); // 페이지 목록 출력 (페이지 접근시 get(i))
		}
	}

	/* @Test
	public void demonstrationRegistrationUpdateStateTest() { // 실증 기업 신청 페이지 승인 / 거부 버튼 클릭 시 상태 변경이 잘되나 테스트 (정상)
		demonstrationRegistrationRepository.updateDemResChangeState(DemonstrationState.ACCEPT, "user14");
	} */

	// --------------------------------- demonstrationRegistration테스트
	// @Test // 실증 제품 삭제 해당 데이터가 삭제되면 해당 번호의 기본키가 비게 되지만 문제 없음. (정상)
	public void demonstrationDeleteTest() { //
		demonstrationRepository.deleteById(Long.valueOf(14));
	}

	// @Test // 실증 상품 여러개의 데이터 삭제 (정상)
	public void demonstrationDeletesTest() {

		List<Long> valueTest = new ArrayList<Long>();

		for (int i = 0; i < 7; i += 2)
			valueTest.add(Long.valueOf(i));

		demonstrationRepository.deleteAllById(valueTest); // deleteAllById: ID리스트를 받음, deleteAll: 엔티티 리스트를 받음

		// @Query로 직접 JPQL DELETE문을 작성해서 여러 엔티티를 삭제할 때는 JPA의 영속성 컨텍스트(Persistence
		// Context)를 직접 거치지 않고, 바로 DB에 DELETE 쿼리를 날립니다.
		// 따라서, JPA가 관리하는 엔티티 상태 변화 감지, 연관된 엔티티에 대한 Cascade 처리, orphanRemoval 등은 전혀 작동하지
		// 않습니다.
	}
/*
	// @Test // 상품 목록 페이지 (정상) <나머지 null값은 사용안할 값임 상관 x>
	public void demonstrationListProductTest() {
		List<Page<DemonstrationListDTO>> allData = new ArrayList<>();
		Page<DemonstrationListDTO> firstPage = demonstrationRepository
				.selectPageDem(PageRequest.of(0, 5, Sort.by("demNum").descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<DemonstrationListDTO> totalPage = demonstrationRepository
					.selectPageDem(PageRequest.of(i, 5, Sort.by("demNum").descending()));
			allData.add(totalPage);
		}

		for (int i = 0; i < totalPageCount; i++) {
			System.out.println(allData.get(i).getContent()); // 페이지 목록 출력 (페이지 접근시 get(i))
		}
	}
*/
	/*
	// @Test // 상품 상세 정보 페이지 (정상)
	public void demonstrationListProductDetailTest() {
		List<Page<DemonstrationListDTO>> allData = new ArrayList<>();
		Page<DemonstrationListDTO> firstPage = demonstrationRepository
				.selectPageDetailDem(PageRequest.of(0, 5, Sort.by("demNum").descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<DemonstrationListDTO> totalPage = demonstrationRepository
					.selectPageDetailDem(PageRequest.of(i, 5, Sort.by("demNum").descending()));
			allData.add(totalPage);
		}

		for (int i = 0; i < totalPageCount; i++) {
			System.out.println(allData.get(i).getContent()); // 페이지 목록 출력 (페이지 접근시 get(i))
		}
	}*/

	/* @Test // 물품 대여 조회 페이지 (정상)
	public void demonstrationListProductViewTest() {
		List<Page<DemonstrationListDTO>> allData = new ArrayList<>();
		Page<DemonstrationListDTO> firstPage = demonstrationRepository
				.selectPageViewDem(PageRequest.of(0, 5, Sort.by("demNum").descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<DemonstrationListDTO> totalPage = demonstrationRepository
					.selectPageViewDem(PageRequest.of(i, 5, Sort.by("demNum").descending()));
			allData.add(totalPage);
		}

		for (int i = 0; i < totalPageCount; i++) {
			System.out.println(allData.get(i).getContent()); // 페이지 목록 출력 (페이지 접근시 get(i))
		}
	}

	// @Test // 물품 대여 조회 페이지 - 검색 테스트 (정상)
	public void demonstrationListProductViewSearchTest() {
		List<Page<DemonstrationListDTO>> allData = new ArrayList<>();
		Page<DemonstrationListDTO> firstPage = demonstrationRepository
				.selectPageViewDem(PageRequest.of(0, 5, Sort.by("demNum").descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<DemonstrationListDTO> totalPage = demonstrationRepository
					.selectPageViewDemSearch(PageRequest.of(i, 5, Sort.by("demNum").descending()), "드");
			allData.add(totalPage);
		}

		for (int i = 0; i < totalPageCount; i++) {
			System.out.println(allData.get(i).getContent()); // 페이지 목록 출력 (페이지 접근시 get(i))
		}
	}
*/
	// @Test // 기업이 상품 등록 정보를 수정하는 페이지 (정상)
	public void demonstrationUpdateProductTest() {
		Demonstration demon = testDemon();
		demonstrationRepository.updateDem(demon.getDemName(), demon.getDemMfr(), demon.getItemNum(), demon.getDemInfo(),
				Long.valueOf(1));

	}

	// --------------------------- demonstration 테스트

	// @Test // 실증 교사 신청 하나 삭제하는 테스트 (정상)
	public void demonstrationReserveDeleteOneTest() {

		demonstrationReserveRepository.deleteOneDemRes(Long.valueOf(11));
	}

	// @Test // 실증 교사 신청 선택한 항목에 대해 삭제하는 테스트 (정상)
	public void demonstrationReserveDeleteSelectedTest() {
		List<Long> lists = new ArrayList<Long>();
		for (int i = 12; i < 22; i++) {
			lists.add(Long.valueOf(i));
		}
		demonstrationReserveRepository.deleteMembersDemRes(lists);
	}

	// @Test // 교사 실증 신청 조회 페이지 (정상)
	public void demonstrationListReserveViewTest() {
		List<Page<DemonstrationListReserveDTO>> allData = new ArrayList<>();
		Page<DemonstrationListReserveDTO> firstPage = demonstrationReserveRepository
				.selectPageDemRes(PageRequest.of(0, 5, Sort.by("demRevNum").descending()));
		int totalPageCount = firstPage.getTotalPages();

		for (int i = 0; i < totalPageCount; i++) {
			Page<DemonstrationListReserveDTO> totalPage = demonstrationReserveRepository
					.selectPageDemRes(PageRequest.of(i, 5, Sort.by("demRevNum").descending()));
			allData.add(totalPage);
		}

		for (int i = 0; i < totalPageCount; i++) {
			System.out.println(allData.get(i).getContent()); // 페이지 목록 출력 (페이지 접근시 get(i))
		}
	}

	
	
	// @Test // 실증 물품 상세 정보 페이지에서 id와 번호를 가져와 예약 변경 시간을 선택 후, 날짜 변경하는 테스트 (정상)
	public void demonstrationResUpdateTest() {
		demonstrationReserveRepository.updateDemResChangeDateAll(LocalDate.parse("2020-12-20"),LocalDate.parse("2050-12-20"),Long.valueOf(10),"user5");
	}

	/*
	//@Test //  물품 대여 조회 페이지에서 연기 신청, 반납 조기 신청 버튼 클릭 시, endDate가 수정되는 테스트 (정상)
	public void demonstrationResUpdate2Test() {
		demonstrationReserveRepository.updateDemResChangeDate(LocalDate.parse("2222-12-22"),Long.valueOf(0));
	}
	*/
	//@Test // 실증 교사 신청 목록 페이지에서 승인 / 거부 버튼 클릭 시, 상태를 변경하는 테스트  (정상)
		public void demonstrationResUpdate3Test() {
			demonstrationReserveRepository.updateDemResChangeState(DemonstrationState.REJECT,"user1",Long.valueOf(10));
	}
		
		
	// --------------------------------- demonstrationReserve 테스트
		
	/*	// @Test // 데이터 리스트 배열 가져와서 해당 날짜가 예약 되있는지 안되있는지 확인하는 데이터를 select하는 테스트 (정상)
		public void demonstrationTimeTest() {
			List<DemonstrationTimeResDTO> demonstrationTimedto=new ArrayList<>();
			List<LocalDate> dates=new ArrayList<LocalDate>();
			dates.add(LocalDate.parse("2025-07-16"));
			dates.add(LocalDate.parse("2025-10-24"));
			demonstrationTimedto=demonstrationTimeRepository.selectDemTime(dates,Long.valueOf(10));
			System.out.println(demonstrationTimedto);
		}	
		*/
		
		//@Test
		@Transactional
		@Commit
		public void testInsertMember() {

			Member member = Member.builder()
					.memId("oj4263")
					.pw(passwordEncoder.encode("qwer1234!@#$"))
					.name("남용진")
					.email("oj4263@test.com")
					.birthDate(LocalDate.of(2000, 7, 5))
					.gender(MemberGender.MALE)
					.phone("01012345678")
					.addr("테스트주소")
					.addrDetail("테스트 상세주소")
					.checkSms(true)
					.checkEmail(false)
					.state(MemberState.NORMAL)
					.role(MemberRole.USER)
					.build();
			memberRepository.save(member);
			memberRepository.flush();
			
		}
		
		 @Test
		public void testDemonstrationReserve1() {
			List<Long> list=new ArrayList<>();
			list.add(200L);
			int result=demonstrationReserveRepository.updateDemResChangeState(DemonstrationState.REJECT, "poi2484", list);
				System.out.println(result);
			}
		
}
