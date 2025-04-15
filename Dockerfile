# 1단계: 빌드용 이미지
FROM gradle:8.6-jdk17-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 캐시 최적화를 위한 순차적 복사
COPY build.gradle settings.gradle gradlew gradle/ ./

# 의존성 캐시
RUN ./gradlew --no-daemon build -x test || return 0

# 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew --no-daemon clean build -x test

# 2단계: 실행용 이미지 (더 작음)
FROM amazoncorretto:17-alpine

WORKDIR /app

# 환경 변수
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS="-Xmx384m -Xms256m -XX:MaxMetaspaceSize=512m -XX:+UseSerialGC"

# 빌드된 JAR만 복사
COPY --from=builder /app/build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar app.jar

# 포트 노출
EXPOSE 80

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar app.jar"]
