# open JDK 21 버전을 기반으로 하는 이미지를 설정할 수 있다.
FROM openjdk:21-jdk-slim

# Gradle을 사용해 빌드를 실행하는 명령어
# CMD ["./gradlew", "clean", "build"]
COPY wait-for-it.sh /wait-for-it.sh

# 컨테이너 내에 /tmp 디렉토리를 볼륨으로 설정
VOLUME /tmp

# Gradle로 빌드한 jar 파일의 위치를 변수로 설정
ARG JAR_FILE=build/libs/chat-app-0.0.1-SNAPSHOT.jar

# JAR_FILE 변수에 지정된 파일을 app.jar라는 이름으로 컨테이너에 추가
COPY ${JAR_FILE} app.jar

# 컨테이너가 사용할 포트를 설정(8080 포트를 사용)
EXPOSE 8080

# 컨테이너가 실행될 때 기본적으로 실행될 명령어를 설정(Java Application을 실행하는 명령어)
#ENTRYPOINT ["/wait-for-it.sh", "db:3306", "--", "java", "-jar", "/app.jar"]
#ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "--", "/wait-for-it.sh", "redis:6379", "--", "java", "-jar", "/app.jar"]
ENTRYPOINT ["/wait-for-it.sh", "mysql:3306", "--", "/wait-for-it.sh", "redis:6379", "--", "java", "-Xms128m", "-Xmx256m", "-jar", "/app.jar"]


