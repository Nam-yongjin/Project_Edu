package com.EduTech.service.member;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.EduTech.dto.member.CompanyDetailDTO;
import com.EduTech.dto.member.CompanyModifyDTO;
import com.EduTech.dto.member.CompanyRegisterDTO;
import com.EduTech.dto.member.KakaoDTO;
import com.EduTech.dto.member.MemberDTO;
import com.EduTech.dto.member.MemberDetailDTO;
import com.EduTech.dto.member.MemberModifyDTO;
import com.EduTech.dto.member.MemberRegisterDTO;
import com.EduTech.dto.member.MemberResetPwDTO;
import com.EduTech.dto.member.StudentDetailDTO;
import com.EduTech.dto.member.StudentModifyDTO;
import com.EduTech.dto.member.StudentRegisterDTO;
import com.EduTech.dto.member.TeacherDetailDTO;
import com.EduTech.dto.member.TeacherModifyDTO;
import com.EduTech.dto.member.TeacherRegisterDTO;
import com.EduTech.entity.demonstration.DemonstrationState;
import com.EduTech.entity.member.Company;
import com.EduTech.entity.member.Member;
import com.EduTech.entity.member.MemberGender;
import com.EduTech.entity.member.MemberRole;
import com.EduTech.entity.member.MemberState;
import com.EduTech.entity.member.Student;
import com.EduTech.entity.member.Teacher;
import com.EduTech.repository.demonstration.DemonstrationRegistrationRepository;
import com.EduTech.repository.demonstration.DemonstrationReserveRepository;
import com.EduTech.repository.member.CompanyRepository;
import com.EduTech.repository.member.MemberRepository;
import com.EduTech.repository.member.StudentRepository;
import com.EduTech.repository.member.TeacherRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

	private final MemberRepository memberRepository;
	private final StudentRepository studentRepository;
	private final TeacherRepository teacherRepository;
	private final CompanyRepository companyRepository;
	private final ModelMapper modelMapper;
	private final PasswordEncoder passwordEncoder;
	private final DemonstrationRegistrationRepository demonstrationRegistrationRepository;
	private final DemonstrationReserveRepository demonstrationReserveRespository;

	// 일반회원 회원가입
	@Override
	@Transactional
	public void registerMember(MemberRegisterDTO memberRegisterDTO) {
		Member member = modelMapper.map(memberRegisterDTO, Member.class);
		member.setPw(passwordEncoder.encode(member.getPw()));
		member.setState(MemberState.NORMAL);
		member.setRole(MemberRole.USER);
		
	    member.setStudent(null);
	    member.setTeacher(null);
	    member.setCompany(null);
	    
		memberRepository.save(member);
	}

	// 학생회원 회원가입
	@Override
	@Transactional
	public void registerStudent(StudentRegisterDTO studentRegisterDTO) {
		Member member = modelMapper.map(studentRegisterDTO, Member.class);
		member.setPw(passwordEncoder.encode(member.getPw()));
		member.setState(MemberState.NORMAL);
		member.setRole(MemberRole.STUDENT);

		Student student = modelMapper.map(studentRegisterDTO, Student.class);
		student.setMemId(null);
		student.setMember(member);
	    member.setStudent(student);
	    member.setTeacher(null);
	    member.setCompany(null);

		memberRepository.save(member);
	}

	// 교사회원 회원가입
	@Override
	@Transactional
	public void registerTeacher(TeacherRegisterDTO teacherRegisterDTO) {
		Member member = modelMapper.map(teacherRegisterDTO, Member.class);
		member.setPw(passwordEncoder.encode(member.getPw()));
		member.setState(MemberState.NORMAL);
		member.setRole(MemberRole.TEACHER);
		
		Teacher teacher = modelMapper.map(teacherRegisterDTO, Teacher.class);
		teacher.setMemId(null);
		teacher.setMember(member);
	    member.setStudent(null);
	    member.setTeacher(teacher);
	    member.setCompany(null);

		memberRepository.save(member);
	}

	// 기업회원 회원가입
	@Override
	@Transactional
	public void registerCompany(CompanyRegisterDTO companyRegisterDTO) {
	    Member member = modelMapper.map(companyRegisterDTO, Member.class);
	    member.setPw(passwordEncoder.encode(companyRegisterDTO.getPw()));
	    member.setState(MemberState.NORMAL);
	    member.setRole(MemberRole.COMPANY);

	    Company company = modelMapper.map(companyRegisterDTO, Company.class);
	    company.setMemId(null);
	    company.setMember(member);
	    member.setStudent(null);
	    member.setTeacher(null);
	    member.setCompany(company);

	    memberRepository.save(member);
	}

	// 아이디 중복 체크
	@Override
	@Transactional
	public boolean isDuplicatedId(String memId) {
		return memberRepository.existsById(memId);
	}

	// 일반회원정보 가져오기
	@Override
	@Transactional(readOnly = true)
	public MemberDetailDTO readMemberInfo(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();
		MemberDetailDTO memberDetailDTO = modelMapper.map(member, MemberDetailDTO.class);

		return memberDetailDTO;
	}

	// 학생회원정보 가져오기
	@Override
	@Transactional(readOnly = true)
	public StudentDetailDTO readStudentInfo(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();

		Student student = studentRepository.findById(memId).orElseThrow();

		StudentDetailDTO studentDetailDTO = new StudentDetailDTO();
		studentDetailDTO.setMemId(member.getMemId());
		studentDetailDTO.setName(member.getName());
		studentDetailDTO.setEmail(member.getEmail());
		studentDetailDTO.setPhone(member.getPhone());
		studentDetailDTO.setBirthDate(member.getBirthDate());
		studentDetailDTO.setGender(member.getGender());
		studentDetailDTO.setAddr(member.getAddr());
		studentDetailDTO.setAddrDetail(member.getAddrDetail());
		studentDetailDTO.setCheckSms(member.isCheckSms());
		studentDetailDTO.setCheckEmail(member.isCheckEmail());
		studentDetailDTO.setSchoolName(student.getSchoolName());

		return studentDetailDTO;
	}

	// 교사회원정보 가져오기
	@Override
	@Transactional(readOnly = true)
	public TeacherDetailDTO readTeacherInfo(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();

		Teacher teacher = teacherRepository.findById(memId).orElseThrow();

		TeacherDetailDTO teacherDetailDTO = new TeacherDetailDTO();
		teacherDetailDTO.setMemId(member.getMemId());
		teacherDetailDTO.setName(member.getName());
		teacherDetailDTO.setEmail(member.getEmail());
		teacherDetailDTO.setPhone(member.getPhone());
		teacherDetailDTO.setBirthDate(member.getBirthDate());
		teacherDetailDTO.setGender(member.getGender());
		teacherDetailDTO.setAddr(member.getAddr());
		teacherDetailDTO.setAddrDetail(member.getAddrDetail());
		teacherDetailDTO.setCheckSms(member.isCheckSms());
		teacherDetailDTO.setCheckEmail(member.isCheckEmail());
		teacherDetailDTO.setSchoolName(teacher.getSchoolName());

		return teacherDetailDTO;
	}

	// 기업회원정보 가져오기
	@Override
	@Transactional(readOnly = true)
	public CompanyDetailDTO readCompanyInfo(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();

		Company company = companyRepository.findById(memId).orElseThrow();

		CompanyDetailDTO companyDetailDTO = new CompanyDetailDTO();
		companyDetailDTO.setMemId(member.getMemId());
		companyDetailDTO.setName(member.getName());
		companyDetailDTO.setEmail(member.getEmail());
		companyDetailDTO.setPhone(member.getPhone());
		companyDetailDTO.setBirthDate(member.getBirthDate());
		companyDetailDTO.setGender(member.getGender());
		companyDetailDTO.setAddr(member.getAddr());
		companyDetailDTO.setAddrDetail(member.getAddrDetail());
		companyDetailDTO.setCheckSms(member.isCheckSms());
		companyDetailDTO.setCheckEmail(member.isCheckEmail());
		companyDetailDTO.setCompanyName(company.getCompanyName());
		companyDetailDTO.setPosition(company.getPosition());

		return companyDetailDTO;
	}

	// 일반회원정보 수정
	@Override
	@Transactional
	public void modifyMemberInfo(String memId, MemberModifyDTO memberModifyDTO) {
		Member member = memberRepository.findById(memId).orElseThrow();
		member.setPw(passwordEncoder.encode(memberModifyDTO.getPw()));
		member.setName(memberModifyDTO.getName());
		member.setEmail(memberModifyDTO.getEmail());
		member.setPhone(memberModifyDTO.getPhone());
		member.setAddr(memberModifyDTO.getAddr());
		member.setAddrDetail(memberModifyDTO.getAddrDetail());
		member.setCheckSms(memberModifyDTO.isCheckSms());
		member.setCheckEmail(memberModifyDTO.isCheckEmail());

		memberRepository.save(member);
	}

	// 학생회원정보 수정
	@Override
	@Transactional
	public void modifyStudentInfo(String memId, StudentModifyDTO studentModifyDTO) {
		Member member = memberRepository.findById(memId).orElseThrow();
		member.setPw(passwordEncoder.encode(studentModifyDTO.getPw()));
		member.setName(studentModifyDTO.getName());
		member.setEmail(studentModifyDTO.getEmail());
		member.setPhone(studentModifyDTO.getPhone());
		member.setAddr(studentModifyDTO.getAddr());
		member.setAddrDetail(studentModifyDTO.getAddrDetail());
		member.setCheckSms(studentModifyDTO.isCheckSms());
		member.setCheckEmail(studentModifyDTO.isCheckEmail());

		Student student = studentRepository.findById(memId).orElseThrow();

		student.setSchoolName(studentModifyDTO.getSchoolName());

		memberRepository.save(member);
	}

	// 교사회원정보 수정
	@Override
	@Transactional
	public void modifyTeacherInfo(String memId, TeacherModifyDTO teacherModifyDTO) {
		Member member = memberRepository.findById(memId).orElseThrow();
		member.setPw(passwordEncoder.encode(teacherModifyDTO.getPw()));
		member.setName(teacherModifyDTO.getName());
		member.setEmail(teacherModifyDTO.getEmail());
		member.setPhone(teacherModifyDTO.getPhone());
		member.setAddr(teacherModifyDTO.getAddr());
		member.setAddrDetail(teacherModifyDTO.getAddrDetail());
		member.setCheckSms(teacherModifyDTO.isCheckSms());
		member.setCheckEmail(teacherModifyDTO.isCheckEmail());

		Teacher teacher = teacherRepository.findById(memId).orElseThrow();

		teacher.setSchoolName(teacherModifyDTO.getSchoolName());

		memberRepository.save(member);
	}

	// 기업회원정보 수정
	@Override
	@Transactional
	public void modifyCompanyInfo(String memId, CompanyModifyDTO companyModifyDTO) {
		Member member = memberRepository.findById(memId).orElseThrow();
		member.setPw(passwordEncoder.encode(companyModifyDTO.getPw()));
		member.setName(companyModifyDTO.getName());
		member.setEmail(companyModifyDTO.getEmail());
		member.setPhone(companyModifyDTO.getPhone());
		member.setAddr(companyModifyDTO.getAddr());
		member.setAddrDetail(companyModifyDTO.getAddrDetail());
		member.setCheckSms(companyModifyDTO.isCheckSms());
		member.setCheckEmail(companyModifyDTO.isCheckEmail());

		Company company = companyRepository.findById(memId).orElseThrow();

		company.setCompanyName(companyModifyDTO.getCompanyName());
		company.setPosition(companyModifyDTO.getPosition());

		memberRepository.save(member);
	}

	// 회원 탈퇴
	@Override
	@Transactional
	public void leaveMember(String memId) {
		Member member = memberRepository.findById(memId).orElseThrow();
		DemonstrationState demonstrationStateReg = demonstrationRegistrationRepository.findByState(memId);
		DemonstrationState demonstrationStateRes = demonstrationReserveRespository.findByState(memId);
		// 현재 예약중인 시설, 행사, 실증대여 있을시 탈퇴불가 처리
		// 역할별 탈퇴 제약 조건 예시
//	    if (member.getRole().equals("COMPANY")) {
//	        if (jobPostRepository.existsByCompanyId(memId)) {
//	            throw new IllegalStateException("등록된 채용공고가 있는 기업은 탈퇴할 수 없습니다.");
//	        }
//	    }

//		if (member.getRole().equals("USER")) {
//
//		}
//		if (member.getRole().equals("STUDENT")) {
//
//		}
//		if (member.getRole().equals("TEACHER")) {
//
//		}
//		if (member.getRole().equals("COMPANY")) {
//
//		}

		// 관리자 계정은 탈퇴불가
		if (member.getRole().equals("ADMIN")) {
			throw new IllegalStateException("관리자는 탈퇴할 수 없습니다.");
		}

		// 블랙리스트 회원일시 탈퇴불가
		if (member.getState().equals("BEN")) {
			throw new IllegalStateException("블랙리스트 회원은 탈퇴할 수 없습니다.");
		}

		if (demonstrationStateReg.equals(DemonstrationState.ACCEPT)) {
			throw new IllegalStateException("실증 등록 중인 회원은 탈퇴할 수 없습니다.");
		}

		if (demonstrationStateRes.equals(DemonstrationState.ACCEPT)) {
			throw new IllegalStateException("실증 신청 중인 회원은 탈퇴할 수 없습니다.");
		}

		member.setState(MemberState.LEAVE);
		memberRepository.save(member);
	}

	// 아이디 찾기
	@Override
	@Transactional
	public String findId(String phone) {
		return memberRepository.findMemIdByPhone(phone);
	}

	// 비밀번호 찾기(변경)
	@Override
	@Transactional
	public void resetPw(String memId, MemberResetPwDTO memberResetPwDTO) {
		Member member = memberRepository.findById(memId)
	            .orElseThrow(() -> new NoSuchElementException("해당 ID의 회원이 존재하지 않습니다."));
		if (memberResetPwDTO.getPhone().equals(member.getPhone())) {
			member.setPw(passwordEncoder.encode(memberResetPwDTO.getPw()));
			memberRepository.save(member);
		}else {
			throw new NoSuchElementException("해당 전화번호로 가입된 아이디가 없습니다.");
		}
	}

	// 카카오 로그인
	@Override
	@Transactional
	public MemberDTO getKakaoMember(String accessToken) {
		KakaoDTO kakaoDTO = getInfoFromKakaoAccessToken(accessToken);
		Optional<Member> result = memberRepository.findByEmail(kakaoDTO.getEmail());

		Member member;
		if (result.isPresent()) { // 회원 로그인
			member = result.get();
		} else { // 자동 회원가입
			member = makeKakaoMember(kakaoDTO);
			memberRepository.save(member);
		}
		MemberDTO memberDTO = entityToDTO(member);
		return memberDTO;
	}

	private KakaoDTO getInfoFromKakaoAccessToken(String accessToken) {
		String kakaoGetUserURL = "https://kapi.kakao.com/v2/user/me";

		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Content-Type", "application/x-www-form-urlencoded");
		HttpEntity<String> entity = new HttpEntity<>(headers);

		UriComponents uriBuilder = UriComponentsBuilder.fromUriString(kakaoGetUserURL).build();
		ResponseEntity<LinkedHashMap> response = restTemplate.exchange(uriBuilder.toString(), HttpMethod.GET, entity,
				LinkedHashMap.class);

		LinkedHashMap<String, Object> body = response.getBody();
		LinkedHashMap<String, Object> kakaoAccount = (LinkedHashMap<String, Object>) body.get("kakao_account");

		try {
			String email = (String) kakaoAccount.get("email");
			String name = (String) kakaoAccount.get("name");
			String genderStr = (String) kakaoAccount.get("gender");
			String birth = (String) kakaoAccount.get("birthday"); // MMDD
			String birthYear = (String) kakaoAccount.get("birthyear"); // YYYY
			String phoneNumber = (String) kakaoAccount.get("phone_number"); // +82 10-XXXX-XXXX
			LocalDate birthDate = null;
			try {
				birthDate = LocalDate.parse(birthYear + birth, DateTimeFormatter.ofPattern("yyyyMMdd"));
			} catch (Exception e) {
				// 파싱 실패 시 처리
				birthDate = null;
			}
			MemberGender gender = genderStr != null && genderStr.equalsIgnoreCase("male") ? MemberGender.MALE
					: MemberGender.FEMALE;

			return KakaoDTO.builder().email(email).name(name).gender(gender).birthDate(birthDate).phone(phoneNumber)
					.build();
		} catch (Exception e) {
			System.out.println("카카오 유저 정보 파싱 중 오류: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}

	}

	// 랜덤 아이디
	private String generateRandomMemId() {
		int length = ThreadLocalRandom.current().nextInt(6, 17); // 6~16자
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = ThreadLocalRandom.current().nextInt(characters.length());
			sb.append(characters.charAt(index));
		}
		return sb.toString();
	}

	// 랜덤 비밀번호
	private String generateRandomPassword() {
		int length = ThreadLocalRandom.current().nextInt(6, 17); // 6~16자
		String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$.";
		StringBuilder sb = new StringBuilder(length);
		for (int i = 0; i < length; i++) {
			int index = ThreadLocalRandom.current().nextInt(characters.length());
			sb.append(characters.charAt(index));
		}
		return sb.toString();
	}

	// 전화번호 유효성 검증
	private String normalizePhoneNumber(String kakaoPhoneNumber) {
		if (kakaoPhoneNumber == null)
			return null;

		// +82 10-1234-5678 -> 01012345678
		String normalized = kakaoPhoneNumber.replaceAll("[^0-9]", ""); // 숫자만 남김

		if (normalized.startsWith("82")) {
			normalized = "0" + normalized.substring(2); // 82 -> 0
		}

		return normalized;
	}

	private Member makeKakaoMember(KakaoDTO kakaoDTO) {
		String memId = generateRandomMemId(); // 랜덤 아이디
		String rawPassword = generateRandomPassword(); // 랜덤 비밀번호
		String encodedPassword = passwordEncoder.encode(rawPassword);
		String normalizedPhone = normalizePhoneNumber(kakaoDTO.getPhone()); // 전화번호 유효성

		System.out.println(memId);
		System.out.println(rawPassword);
		System.out.println(kakaoDTO.getName());
		System.out.println(kakaoDTO.getEmail());
		System.out.println(kakaoDTO.getBirthDate());
		System.out.println(kakaoDTO.getGender());
		System.out.println(normalizedPhone);

		return Member.builder().memId(memId).pw(encodedPassword).name(kakaoDTO.getName()).email(kakaoDTO.getEmail())
				.birthDate(kakaoDTO.getBirthDate()).gender(kakaoDTO.getGender()).phone(normalizedPhone)
				.state(MemberState.NORMAL).role(MemberRole.USER).kakao(kakaoDTO.getEmail()).build();
	}

}
