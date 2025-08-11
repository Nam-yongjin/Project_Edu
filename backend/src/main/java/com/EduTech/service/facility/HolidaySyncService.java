package com.EduTech.service.facility;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriComponentsBuilder;

import com.EduTech.entity.facility.PublicHoliday;
import com.EduTech.repository.facility.PublicHolidayRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class HolidaySyncService {

    private final PublicHolidayRepository publicHolidayRepository;
    private final RestTemplateBuilder restTemplateBuilder;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.holiday.service-key}")
    private String serviceKey; // Encoding 키(% 포함) 사용

    private static final String ENDPOINT =
            "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

    private static final DateTimeFormatter KASI_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    @Transactional
    public int syncYear(int year) {
        log.info("공휴일 동기화 시작: year={}", year);
        List<HolidayDto> external = fetchFromKasi(year);

        int saved = 0;
        for (HolidayDto h : external) {
            if (!publicHolidayRepository.existsByDate(h.date())) {
                publicHolidayRepository.save(
                        PublicHoliday.builder()
                                .date(h.date())
                                .name(h.name())
                                .isLunar(false)
                                .build()
                );
                saved++;
            }
        }
        log.info("공휴일 동기화 완료: year={}, 수집={}건, 신규저장={}건", year, external.size(), saved);
        return saved;
    }

    @Transactional
    public int syncThisAndNextYear() {
        int y = LocalDate.now().getYear();
        int total = syncYear(y) + syncYear(y + 1);
        log.info("올해/내년 공휴일 동기화 결과: 총 {}건 신규 저장", total);
        return total;
    }

    private List<HolidayDto> fetchFromKasi(int year) {
        List<HolidayDto> result = new ArrayList<>();

        if (serviceKey == null || serviceKey.isBlank()) {
            log.warn("KASI serviceKey가 비어있습니다. app.holiday.service-key(Encoding 키)를 확인하세요.");
            return result;
        }

        // Encoding 키 재인코딩 방지
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.NONE);

        RestTemplate rt = restTemplateBuilder
                .uriTemplateHandler(factory)
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();

        for (int month = 1; month <= 12; month++) {
            try {
                String url = UriComponentsBuilder
                        .fromHttpUrl(ENDPOINT)
                        .queryParam("ServiceKey", serviceKey) // 현재 환경에서 동작 확인된 형태
                        .queryParam("solYear", year)
                        .queryParam("solMonth", String.format("%02d", month))
                        .queryParam("numOfRows", 100)
                        .queryParam("pageNo", 1)
                        .queryParam("_type", "json")
                        .build(true)
                        .toUriString();

                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

                ResponseEntity<String> resp = rt.exchange(url, HttpMethod.GET, httpEntity, String.class);
                String body = resp.getBody();
                String ctype = resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

                if (body == null || body.isBlank()) continue;

                boolean parsed = false;

                // JSON 우선
                try {
                    if ((ctype != null && ctype.toLowerCase().contains("json")) || body.trim().startsWith("{")) {
                        JsonNode node = objectMapper.readTree(body);
                        JsonNode items = node.path("response").path("body").path("items").path("item");
                        if (items.isArray()) {
                            for (JsonNode it : items) addIfHoliday(result, it);
                        } else if (items.isObject()) {
                            addIfHoliday(result, items);
                        }
                        parsed = true;
                    }
                } catch (Exception ignore) { }

                // XML 폴백
                if (!parsed) {
                    Document doc = Jsoup.parse(body, "", Parser.xmlParser());

                    // 오류 응답이면 다음 달로
                    Element err = doc.selectFirst("OpenAPI_ServiceResponse > cmmMsgHeader");
                    if (err != null) continue;

                    for (Element item : doc.select("item")) {
                        String isHoliday = text(item.selectFirst("isHoliday"));
                        if (!"Y".equalsIgnoreCase(isHoliday)) continue;
                        String locdateStr = text(item.selectFirst("locdate"));
                        String dateName   = text(item.selectFirst("dateName"));
                        if (locdateStr.isBlank()) continue;
                        LocalDate date = LocalDate.parse(locdateStr, KASI_FMT);
                        result.add(new HolidayDto(date, dateName));
                    }
                }

            } catch (Exception e) {
                // 월 단위 요약만 유지
                log.warn("KASI 호출/파싱 실패: year={}, month={}, msg={}", year, month, e.getMessage());
            }
        }
        return result;
    }

    private void addIfHoliday(List<HolidayDto> list, JsonNode it) {
        if (!"Y".equalsIgnoreCase(it.path("isHoliday").asText())) return;
        String dateStr = it.path("locdate").asText("");
        if (dateStr.isBlank()) return;
        LocalDate date = LocalDate.parse(dateStr, KASI_FMT);
        list.add(new HolidayDto(date, it.path("dateName").asText("")));
    }

    private static String text(Element el) {
        return el != null ? el.text() : "";
    }

    private record HolidayDto(LocalDate date, String name) {}
}