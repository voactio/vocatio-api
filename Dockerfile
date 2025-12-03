FROM eclipse-temurin:21-jre-jammy

ARG JAR_FILE=target/vocatio-api-0.0.1.jar
WORKDIR /app
COPY ${JAR_FILE} vocatio-api.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar","vocatio-api.jar"]