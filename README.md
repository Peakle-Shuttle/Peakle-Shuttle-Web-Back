# Peakle-Shuttle-Web-Back


| `./gradlew bootRun --args='--spring.profiles.active=local'` | 애플리케이션 실행 |
Swagger UI: http://localhost:8080/swagger-ui.html
API 문서 (JSON): http://localhost:8080/v3/api-docs


docker 디렉터리 구조

docker/
├── Dockerfile
├── docker-compose.yml
├── .env.example
└── app/              # JAR 파일을 여기에 복사
사용 방법

# 1. JAR 빌드
.\gradlew clean bootJar

# 2. JAR 파일을 docker/app/ 폴더로 복사
cp build/libs/shuttle-0.0.1-SNAPSHOT.jar docker/app/


# Docker Compose 실행 (EC2에선 docker compose)
docker-compose up -d --build
docker-compose up