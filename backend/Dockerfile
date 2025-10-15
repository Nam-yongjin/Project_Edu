# 빌드 스테이지
FROM gradle:8-jdk17 AS build

WORKDIR /app

# Gradle 설정 파일 복사
COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

# 의존성 다운로드 (캐싱용)
RUN gradle dependencies --no-daemon || return 0

# 소스 코드 복사
COPY src ./src

# 빌드 (테스트 스킵)
RUN gradle clean build --no-daemon -x test

# 실행 스테이지
FROM openjdk:17-slim

WORKDIR /app

# 빌드된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 포트 노출
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]