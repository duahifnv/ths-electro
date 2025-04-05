FROM maven:3.9.9-eclipse-temurin-21-alpine

ARG PROJECT

WORKDIR /ths-electro
COPY pom.xml /ths-electro
COPY /$PROJECT /ths-electro/$PROJECT

WORKDIR /ths-electro/$PROJECT

CMD ["mvn", "spring-boot:run"]