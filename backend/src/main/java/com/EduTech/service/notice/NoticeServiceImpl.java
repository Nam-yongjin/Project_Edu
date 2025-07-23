package com.EduTech.service.notice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.notice.NoticeCreateRegisterDTO;
import com.EduTech.dto.notice.NoticeDetailDTO;
import com.EduTech.dto.notice.NoticeFileDTO;
import com.EduTech.dto.notice.NoticeListDTO;
import com.EduTech.dto.notice.NoticeSearchDTO;
import com.EduTech.dto.notice.NoticeUpdateRegisterDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.notice.Notice;
import com.EduTech.entity.notice.NoticeFile;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.notice.NoticeFileRepository;
import com.EduTech.repository.notice.NoticeRepository;
import com.EduTech.repository.notice.NoticeSpecifications;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.util.FileUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor // final 필드에 대한 생성자를 자동으로 생성
@Transactional
public class NoticeServiceImpl implements NoticeService {

	private final NoticeRepository noticeRepository;
	private final NoticeFileRepository noticeFileRepository;
	private final MemberRepository memberRepository; // MemberEntity 가져오려면 필요
	private final ModelMapper modelMapper; // DTO <-> Entity 매핑을 위해 필요
	private final FileUtil fileUtil; // 파일 처리를 위해 필요

	// 공지사항 등록
	@Override // 상위 타입 메서드 재정의
	public void createNotice(NoticeCreateRegisterDTO dto, List<MultipartFile> file) {
		// JWT에서 사용자 조회(Controller에서 관리자만 작성하게 하려면 필요)
		Member member = memberRepository.findById(JWTFilter.getMemId())
				// 해당 아이디로 사용자를 찾지 못한 경우 예외 발생
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다." + JWTFilter.getMemId()));
		// DTO를 Notice Entity로 변환(ModelMapper 사용)
		Notice notice = modelMapper.map(dto, Notice.class);
		notice.setCreatedAt(LocalDateTime.now()); // 작성 시간 설정
		notice.setMember(member); // 작성자 설정
		notice.setView(0L); // 조회수 기본값 0으로 설정

		if (file != null && !file.isEmpty() && !file.get(0).isEmpty()) { // 파일이 실제로 업로드 되어있는지 확인
			// 파일 저장
			String dirName = "noticeFile"; // 저장할 디렉토리 이름
			List<Object> fileMapList = fileUtil.saveFiles(file, dirName); // Map 형태의 리스트로 반환

			if (fileMapList != null && !fileMapList.isEmpty()) { // 실제로 업로드 된 파일이 존재할 때
				// List<object>형식으로 반환된 fileMapList를 List<NoticeFile로 변환
				List<NoticeFile> noticeFiles = fileMapList.stream()
						// fileMapList는 List<object>타입 --> 안에 들어있는 객체는 Map<String, String>타입이라 변환
						.map(obj -> (Map<String, String>) obj).filter(fileData -> {
							String type = fileData.get("fileType");
							// 확장자 검증 --> 특정 문자열이 포함되어있는지 확인(대소문자 구분을 없애기 위해 소문자로 변환)
							return type != null && List.of("jpg", "png", "hwp", "pdf").contains(type.toLowerCase());
						})// null제외 file타입 없으면 필터링, 리스트에 포함된 확장자만 통과
						.map(fileData -> { // Map을 Notice Entity로 변환
							NoticeFile noticeFile = new NoticeFile();
							noticeFile.setOriginalName(fileData.get("originalName"));
							noticeFile.setFilePath(fileData.get("filePath"));
							noticeFile.setFileType(fileData.get("fileType").toLowerCase());
							noticeFile.setNotice(notice); // 공지사항과 연관관계 설정

							return noticeFile;
						}).collect(Collectors.toList()); // stream을 List<NoticeFile>로 수집

				notice.setNoticeFile(noticeFiles); // Notice에 파일 리스트 연결(연관관계 설정)
				noticeRepository.save(notice); // DB에 저장
				// cascade 사용 + 중복 방지로 나눠서 작성
			}

		}

	}

	// 공지사항 수정
	@Override
	public void updateNotice(NoticeUpdateRegisterDTO dto, List<MultipartFile> file, Long noticeNum) {
		Notice notice = noticeRepository.findById(noticeNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다. 번호: " + noticeNum));
		// ModelMapper로 DTO데이터를 Entity에 매핑
		modelMapper.map(dto, notice);
		notice.setUpdatedAt(LocalDateTime.now());

		// 기존 파일 처리
		if (file != null && !file.isEmpty() && !file.get(0).isEmpty()) {
			// 기존 파일 삭제
			deleteExistingFile(notice.getNoticeNum());

			// 새 파일 저장(createNotice랑 동일)
			String dirName = "noticeFile";
			List<Object> fileMapList = fileUtil.saveFiles(file, dirName);

			if (fileMapList != null && !fileMapList.isEmpty()) {

				List<NoticeFile> noticeFiles = fileMapList.stream()

						.map(obj -> (Map<String, String>) obj).filter(fileData -> {
							String type = fileData.get("fileType");

							return type != null && List.of("jpg", "png", "hwp", "pdf").contains(type.toLowerCase());
						}).map(fileData -> {
							NoticeFile noticeFile = new NoticeFile();
							noticeFile.setOriginalName(fileData.get("originalName"));
							noticeFile.setFilePath(fileData.get("filePath"));
							noticeFile.setFileType(fileData.get("fileType").toLowerCase());
							noticeFile.setNotice(notice);

							return noticeFile;
						}).collect(Collectors.toList());

				notice.setNoticeFile(noticeFiles);
				noticeRepository.save(notice);
			}

		}

	}

	// Helper Method(재귀함수가 복잡해질 때 사용)
	private void deleteExistingFile(Long noticeNum) { // 해당 공지사항에 연결된 파일목록 가져옴
		List<NoticeFile> existingFile = noticeFileRepository.findByNotice_NoticeNum(noticeNum);
		// 각 파일의 저장경로만 추출해서 리스트로 변환
		List<String> filePaths = existingFile.stream().map(NoticeFile::getFilePath) // NoticeFile에 속한 경로
				.collect(Collectors.toList());
		// 경로들을 deleteFiles메서드에 넘겨서 실제 파일 삭제
		fileUtil.deleteFiles(filePaths);
		// DB에서도 NoticeFile 데이터들을 삭제
		noticeFileRepository.deleteAll(existingFile);
	};

	// 공지사항 삭제(단일)
	@Override
	public void deleteNotice(Long noticeNum) {
		Notice notice = noticeRepository.findById(noticeNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다. 번호: " + noticeNum));

		deleteExistingFile(noticeNum);
		noticeRepository.delete(notice);
	}

	// 공지사항 삭제(일괄)
	@Override
	public void deleteNotices(List<Long> noticeNums) {
		List<Notice> noticeList = noticeRepository.findAllById(noticeNums);

		if (noticeList.size() != noticeNums.size()) {
			// 어떤 번호가 존재하지 않는지 찾아내기
			List<Long> foundIds = noticeList.stream().map(Notice::getNoticeNum).collect(Collectors.toList());
			List<Long> notFoundIds = noticeNums.stream().filter(id -> !foundIds.contains(id))
					.collect(Collectors.toList());

			throw new IllegalArgumentException("존재하지 않는 공지사항 번호: " + notFoundIds);
		}

		noticeNums.forEach(this::deleteExistingFile);
		noticeRepository.deleteAll(noticeList);
	}

	// 공지사항 상세 조회
	@Override
	@Transactional(readOnly = true) // 읽기 전용
	public NoticeDetailDTO getNoticeDetail(Long noticeNum) {
		Notice notice = noticeRepository.findById(noticeNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다. 번호: " + noticeNum));

		// ModelMapper로 기본 매핑
		NoticeDetailDTO noticeDetailDTO = modelMapper.map(notice, NoticeDetailDTO.class);

		// 첨부파일 정보 매핑
		List<NoticeFileDTO> file = notice.getNoticeFile().stream()
				.map(noticeFile -> modelMapper.map(noticeFile, NoticeFileDTO.class)).collect(Collectors.toList());

		noticeDetailDTO.setFileDTO(file);
		return noticeDetailDTO;
	}

	// 공지사항 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<NoticeListDTO> getNoticeList(NoticeSearchDTO dto, Pageable pageable) {
		// 검색 조건을 Specification으로 변환
		Specification<Notice> spec = NoticeSpecifications.fromDTO(dto);

		// 조건에 맞는 공지사항 페이징 조회
		Page<Notice> noticeList = noticeRepository.findAll(spec, pageable);

		// 엔티티 -> DTO 변환
		Page<NoticeListDTO> result = noticeList.map(notice -> {
			NoticeListDTO noticeListDTO = new NoticeListDTO();

			// ModelMapper로 기본 매핑
			modelMapper.map(notice, noticeListDTO);

			// 작성자 이름 설정
			noticeListDTO.setName(notice.getMember().getName());

			return noticeListDTO;
		});

		return result;

	}

	// 고정된 공지만 조회
	@Override
	@Transactional(readOnly = true)
	public List<NoticeListDTO> findPinned() {
		Sort sort = Sort.by("createdAt").descending(); // 작성일 기준으로 내림차순
		List<Notice> noticeList = noticeRepository.findAllByIsPinned(true, sort); // 고정된 공지 -> 작성일 기준 내림차순으로 리스트

		List<NoticeListDTO> dtoList = noticeList.stream().map(notice -> {
			NoticeListDTO noticeListDTO = modelMapper.map(notice, NoticeListDTO.class); // 엔티티를 DTO로 매핑
			if (notice.getMember() != null) { // member가 null이 아닐 때
				noticeListDTO.setName(notice.getMember().getName()); // 작성자 설정
			}
			return noticeListDTO; // noticeListDTO로 반환
		}).collect(Collectors.toList()); // 리스트로 수집

		return dtoList; // dtoList로 반환

	}

	// 상세 공지 조회 시 조회수 증가
	@Override
	public void increaseView(Long noticeNum) {
		Notice notice = noticeRepository.findById(noticeNum)
		        .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 존재하지 않습니다. 번호: " + noticeNum));
		notice.setView(notice.getView() + 1);

	}

}
