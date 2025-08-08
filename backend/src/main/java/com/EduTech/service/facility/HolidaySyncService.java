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
    private final ObjectMapper objectMapper = new ObjectMapper(); // 필요시 @Bean 주입으로 바꿔도 OK

    @Value("${app.holiday.service-key}")
    private String serviceKey; // **디코딩된 원문 키**(+, /, =) 그대로 넣어두세요.

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
            log.warn("KASI serviceKey가 비어있습니다. app.holiday.service-key를 확인하세요.");
            return result;
        }

        RestTemplate rt = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();

        for (int month = 1; month <= 12; month++) {
            try {
                // 🔧 포인트: 디코딩된 키를 주고, 빌더가 인코딩하게 한다
            	String url = UriComponentsBuilder
            		    .fromHttpUrl(ENDPOINT)
            		    .queryParam("serviceKey", serviceKey) // 원문 키(+, /, =) 그대로
            		    .queryParam("solYear", year)
            		    .queryParam("solMonth", String.format("%02d", month))
            		    .queryParam("numOfRows", 100)
            		    .queryParam("pageNo", 1)
            		    .queryParam("_type", "json")
            		    .build()            // true 쓰지 말기
            		    .encode()
            		    .toUriString();

                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                HttpEntity<Void> httpEntity = new HttpEntity<>(headers);

                ResponseEntity<String> resp = rt.exchange(url, HttpMethod.GET, httpEntity, String.class);

                String body = resp.getBody();
                String ctype = resp.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE);

                if (body == null || body.isBlank()) {
                    log.warn("KASI 응답이 비어있음: y={}, m={}", year, month);
                    continue;
                }

                // 1) JSON 시도
                int addedThisMonth = 0;
                boolean parsed = false;
                try {
                    if ((ctype != null && ctype.toLowerCase().contains("json")) || body.trim().startsWith("{")) {
                        JsonNode node = objectMapper.readTree(body);
                        String code = node.path("response").path("header").path("resultCode").asText("");
                        String msg = node.path("response").path("header").path("resultMsg").asText("");
                        JsonNode items = node.path("response").path("body").path("items").path("item");

                        if (items.isArray()) {
                            for (JsonNode it : items) addedThisMonth += addIfHoliday(result, it);
                        } else if (items.isObject()) {
                            addedThisMonth += addIfHoliday(result, items);
                        }

                        log.info("KASI(JSON) y={} m={} code={} msg={} items={} addedY={}",
                                year, String.format("%02d", month),
                                code, msg,
                                items.isArray() ? items.size() : (items.isObject() ? 1 : 0),
                                addedThisMonth);
                        parsed = true;
                    }
                } catch (Exception jsonEx) {
                    // JSON 파싱 실패 → XML 폴백
                }

                // 2) XML 폴백
                if (!parsed) {
                    int before = result.size();
                    Document doc = Jsoup.parse(body, "", Parser.xmlParser());
                    String resultCode = text(doc.selectFirst("resultCode"));
                    String resultMsg = text(doc.selectFirst("resultMsg"));
                    for (Element item : doc.select("item")) {
                        String isHoliday = text(item.selectFirst("isHoliday"));
                        if (!"Y".equalsIgnoreCase(isHoliday)) continue;
                        String locdateStr = text(item.selectFirst("locdate"));
                        String dateName = text(item.selectFirst("dateName"));
                        if (locdateStr.isBlank()) continue;
                        LocalDate date = LocalDate.parse(locdateStr, KASI_FMT);
                        result.add(new HolidayDto(date, dateName));
                    }
                    addedThisMonth = result.size() - before;
                    log.info("KASI(XML)  y={} m={} code={} msg={} items={} addedY={}",
                            year, String.format("%02d", month),
                            resultCode, resultMsg, addedThisMonth, addedThisMonth);
                }

            } catch (Exception e) {
                log.error("KASI 호출/파싱 실패: y={}, m={}, cause={}", year, month, e.toString());
                log.debug("stacktrace", e);
            }
        }
        return result;
    }

    private int addIfHoliday(List<HolidayDto> list, JsonNode it) {
        if (!"Y".equalsIgnoreCase(it.path("isHoliday").asText())) return 0;
        String dateStr = it.path("locdate").asText("");
        if (dateStr.isBlank()) return 0;
        LocalDate date = LocalDate.parse(dateStr, KASI_FMT);
        list.add(new HolidayDto(date, it.path("dateName").asText("")));
        return 1;
    }

    private static String text(Element el) {
        return el != null ? el.text() : "";
    }

    private record HolidayDto(LocalDate date, String name) {}
}
