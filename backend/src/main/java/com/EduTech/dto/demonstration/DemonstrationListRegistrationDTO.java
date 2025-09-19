package com.EduTech.dto.demonstration;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.EduTech.entity.demonstration.DemonstrationState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DemonstrationListRegistrationDTO { // 관리자 실증 신청 내역 조회

	private Long demRegNum; // demonstration_registration의 기본키
	private Long demNum; // 상품 번호
	private LocalDate regDate; // 등록 일자를 현재 시간으로 설정
	private LocalDate expDate; // 반납 예정 일자
	private DemonstrationState state; // 상태의 디폴트 값을 WAIT 상태로 저장
	private String memId; // 회원 아이디
	private String companyName; // 기업명
	private String demName; // 상품 이름
	private String addr; // 주소
	private String addrDetail; // 세부 주소
	private String phone;  // 핸드폰 번호
	private Long itemNum; // 재고량
	private DemonstrationImageDTO mainImage; // 메인 이미지 
	
	 // Repository 쿼리용 생성자 (imageList 제외)
    public DemonstrationListRegistrationDTO(Long demRegNum, Long demNum, LocalDate regDate, 
            LocalDate expDate, DemonstrationState state, String memId, String companyName, 
            String demName, String addr, String addrDetail, String phone, Long itemNum) {
        this.demRegNum = demRegNum;
        this.demNum = demNum;
        this.regDate = regDate;
        this.expDate = expDate;
        this.state = state;
        this.memId = memId;
        this.companyName = companyName;
        this.demName = demName;
        this.addr = addr;
        this.addrDetail = addrDetail;
        this.phone = phone;
        this.itemNum = itemNum;
    }
}