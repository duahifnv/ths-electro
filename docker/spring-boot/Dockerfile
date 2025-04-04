FROM maven:3.9.9-eclipse-temurin-21-alpine

ARG PROJECT

WORKDIR /envelope-web
COPY pom.xml /envelope-web
COPY /$PROJECT /envelope-web/$PROJECT

WORKDIR /envelope-web/$PROJECT

CMD ["mvn", "spring-boot:run"]