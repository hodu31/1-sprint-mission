FROM amazoncorretto:17-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 프로젝트 정보 및 JVM 옵션을 위한 환경 변수 설정
ENV PROJECT_NAME=discodeit \
    PROJECT_VERSION=1.2-M8 \
    JVM_OPTS=""

# 전체 프로젝트 파일 복사
COPY . .

# Gradle Wrapper 권한 부여 및 빌드 진행 (빌드 시 테스트 생략)
RUN chmod +x ./gradlew && ./gradlew clean build -x test

# 포트 노출 (80 포트)
EXPOSE 80

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JVM_OPTS -jar build/libs/${PROJECT_NAME}-${PROJECT_VERSION}.jar"]