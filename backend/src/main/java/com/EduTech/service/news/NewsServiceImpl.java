package com.EduTech.service.news;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.EduTech.dto.news.NewsCreateRegisterDTO;
import com.EduTech.dto.news.NewsDetailDTO;
import com.EduTech.dto.news.NewsFileDTO;
import com.EduTech.dto.news.NewsListDTO;
import com.EduTech.dto.news.NewsSearchDTO;
import com.EduTech.dto.news.NewsUpdateRegisterDTO;
import com.EduTech.dto.notice.NoticeDetailDTO;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.news.News;
import com.EduTech.entity.news.NewsFile;
import com.EduTech.entity.notice.Notice;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.news.NewsFileRepository;
import com.EduTech.repository.news.NewsRepository;
import com.EduTech.repository.news.NewsSpecifications;
import com.EduTech.security.jwt.JWTFilter;
import com.EduTech.util.FileUtil;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class NewsServiceImpl implements NewsService {

	private final NewsRepository newsRepository;
	private final NewsFileRepository newsFileRepository;
	private final MemberRepository memberRepository; // MemberEntity 가져오려면 필요
	private final ModelMapper modelMapper; // DTO <-> Entity 매핑을 위해 필요
	private final FileUtil fileUtil; // 파일 처리를 위해 필요
	
	//ModelMapper configuration errors:
	//Test 돌릴 때 ModelMapper가 경로를 혼동하면 넣어줌
	//어떤 경로 사용할지 지정
	@PostConstruct
	public void setupModelMapper() {
	    modelMapper.getConfiguration().setAmbiguityIgnored(true); //모호함 허용
	    modelMapper.typeMap(Notice.class, NewsDetailDTO.class)
	        .addMapping(src -> src.getMember().getMemId(), NewsDetailDTO::setWriterMemid);
	}

	// 언론보도 등록
	@Override // 상위 타입 메서드 재정의
	public void createNews(NewsCreateRegisterDTO dto, List<MultipartFile> file) {
		// JWT에서 사용자 조회(Controller에서 관리자만 작성하게 하려면 필요)
		Member member = memberRepository.findById(JWTFilter.getMemId())
				// 해당 아이디로 사용자를 찾지 못한 경우 예외 발생
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다." + JWTFilter.getMemId()));
		// DTO를 News Entity로 변환(ModelMapper 사용)
		News news = modelMapper.map(dto, News.class);
		news.setCreatedAt(LocalDateTime.now()); // 작성 시간 설정
		news.setMember(member); // 작성자 설정
		news.setView(0L); // 조회수 기본값 0으로 설정

		if (file != null && !file.isEmpty() && !file.get(0).isEmpty()) { // 파일이 실제로 업로드 되어있는지 확인
			// 파일 저장
			String dirName = "newsFile"; // 저장할 디렉토리 이름
			List<Object> fileMapList = fileUtil.saveFiles(file, dirName); // Map 형태의 리스트로 반환

			if (fileMapList != null && !fileMapList.isEmpty()) { // 실제로 업로드 된 파일이 존재할 때
				// List<object>형식으로 반환된 fileMapList를 List<NewsFile로 변환
				List<NewsFile> newsFiles = fileMapList.stream()
						// fileMapList는 List<object>타입 --> 안에 들어있는 객체는 Map<String, String>타입이라 변환
						.map(obj -> (Map<String, String>) obj).filter(fileData -> {
							String type = fileData.get("fileType");
							// 확장자 검증 --> 특정 문자열이 포함되어있는지 확인(대소문자 구분을 없애기 위해 소문자로 변환)
							return type != null && List.of("jpg", "png", "hwp", "pdf").contains(type.toLowerCase());
						})// null제외 file타입 없으면 필터링, 리스트에 포함된 확장자만 통과
						.map(fileData -> { // Map을 News Entity로 변환
							NewsFile newsFile = new NewsFile();
							newsFile.setOriginalName(fileData.get("originalName"));
							newsFile.setFilePath(fileData.get("filePath"));
							newsFile.setFileType(fileData.get("fileType").toLowerCase());
							newsFile.setNews(news); // 언론보도와 연관관계 설정

							return newsFile;
						}).collect(Collectors.toList()); // stream을 List<NewsFile>로 수집

				news.setNewsFile(newsFiles); // News에 파일 리스트 연결(연관관계 설정)
				newsRepository.save(news); // DB에 저장
				// cascade 사용 + 중복 방지로 나눠서 작성
			}

		}

	}

	// 언론보도 수정
	@Override
	public void updateNews(NewsUpdateRegisterDTO dto, List<MultipartFile> file, Long newsNum) {
		News news = newsRepository.findById(newsNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 뉴스가 존재하지 않습니다. 번호: " + newsNum));
		// ModelMapper로 DTO데이터를 Entity에 매핑
		modelMapper.map(dto, news);
		news.setUpdatedAt(LocalDateTime.now());

		// 기존 파일 처리
		if (file != null && !file.isEmpty() && !file.get(0).isEmpty()) {
			// 기존 파일 삭제
			deleteExistingFile(news.getNewsNum());

			// 새 파일 저장(createNotice랑 동일)
			String dirName = "newsFile";
			List<Object> fileMapList = fileUtil.saveFiles(file, dirName);

			if (fileMapList != null && !fileMapList.isEmpty()) {

				List<NewsFile> newsFiles = fileMapList.stream()

						.map(obj -> (Map<String, String>) obj).filter(fileData -> {
							String type = fileData.get("fileType");

							return type != null && List.of("jpg", "png", "hwp", "pdf").contains(type.toLowerCase());
						}).map(fileData -> {
							NewsFile newsFile = new NewsFile();
							newsFile.setOriginalName(fileData.get("originalName"));
							newsFile.setFilePath(fileData.get("filePath"));
							newsFile.setFileType(fileData.get("fileType").toLowerCase());
							newsFile.setNews(news);

							return newsFile;
						}).collect(Collectors.toList());

				news.setNewsFile(newsFiles);
				newsRepository.save(news);
			}

		}

	}

	// Helper Method(재귀함수가 복잡해질 때 사용)
	private void deleteExistingFile(Long newsNum) { // 해당 보도자료에 연결된 파일목록 가져옴
		List<NewsFile> existingFile = newsFileRepository.findByNews_NewsNum(newsNum);
		// 각 파일의 저장경로만 추출해서 리스트로 변환
		List<String> filePaths = existingFile.stream().map(NewsFile::getFilePath) // NewsFile에 속한 경로
				.collect(Collectors.toList());
		// 경로들을 deleteFiles메서드에 넘겨서 실제 파일 삭제
		fileUtil.deleteFiles(filePaths);
		// DB에서도 NewsFile 데이터들을 삭제
		newsFileRepository.deleteAll(existingFile);
	};

	// 언론보도 삭제(단일)
	@Override
	public void deleteNews(Long newsNum) {
		News news = newsRepository.findById(newsNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 뉴스가 존재하지 않습니다. 번호: " + newsNum));

		deleteExistingFile(newsNum);
		newsRepository.delete(news);
	}

	// 언론보도 삭제(일괄)
	@Override
	public void deleteNewsByIds(List<Long> newsNums) {
		List<News> newsList = newsRepository.findAllById(newsNums);

		if (newsList.size() != newsNums.size()) {
			// 어떤 번호가 존재하지 않는지 찾아내기
			List<Long> foundIds = newsList.stream().map(News::getNewsNum).collect(Collectors.toList());
			List<Long> notFoundIds = newsNums.stream().filter(id -> !foundIds.contains(id))
					.collect(Collectors.toList());

			throw new IllegalArgumentException("존재하지 않는 뉴스 번호: " + notFoundIds);
		}

		newsNums.forEach(this::deleteExistingFile);
		newsRepository.deleteAll(newsList);
	}

	// 언론보도 상세 조회
	@Override
	@Transactional(readOnly = true) // 읽기 전용
	public NewsDetailDTO getNewsDetail(Long newsNum) {
		News news = newsRepository.findById(newsNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 뉴스가 존재하지 않습니다. 번호: " + newsNum));

		// ModelMapper로 기본 매핑
		NewsDetailDTO newsDetailDTO = modelMapper.map(news, NewsDetailDTO.class);

		// 첨부파일 정보 매핑
		List<NewsFileDTO> file = news.getNewsFile().stream()
				.map(newsFile -> modelMapper.map(newsFile, NewsFileDTO.class)).collect(Collectors.toList());

		newsDetailDTO.setFileDTO(file);
		return newsDetailDTO;
	}

	// 언론보도 전체 조회
	@Override
	@Transactional(readOnly = true)
	public Page<NewsListDTO> getNewsList(NewsSearchDTO dto, Pageable pageable) {
		// 검색 조건을 Specification으로 변환
		Specification<News> spec = NewsSpecifications.fromDTO(dto);

		// 조건에 맞는 보도자료 페이징 조회
		Page<News> newsList = newsRepository.findAll(spec, pageable);

		// 엔티티 -> DTO 변환
		Page<NewsListDTO> result = newsList.map(news -> {
			NewsListDTO newsListDTO = new NewsListDTO();

			// ModelMapper로 기본 매핑
			modelMapper.map(news, newsListDTO);

			// 작성자 이름 설정
			newsListDTO.setName(news.getMember().getName());

			return newsListDTO;
		});

		return result;

	}

	// 상세 언론 조회 시 조회수 증가
	@Override
	public void increaseView(Long newsNum) {
		News news = newsRepository.findById(newsNum)
				.orElseThrow(() -> new IllegalArgumentException("해당 뉴스가 존재하지 않습니다. 번호: " + newsNum));
		news.setView(news.getView() + 1);

	}

}
