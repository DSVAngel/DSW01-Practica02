FROM maven:3.9.8-eclipse-temurin-17 AS builder
WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests clean package

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Build arguments (se pasan desde el workflow de GitHub o docker build)
ARG DB_URL=jdbc:postgresql://localhost:5432/dswdb?sslmode=disable
ARG DB_USERNAME=postgres
ARG DB_PASSWORD=postgres
ARG SERVER_PORT=8080

# Environment variables (pueden ser sobrescritas en runtime)
ENV DB_URL=${DB_URL}
ENV DB_USERNAME=${DB_USERNAME}
ENV DB_PASSWORD=${DB_PASSWORD}
ENV SERVER_PORT=${SERVER_PORT}

ENTRYPOINT ["java", \
  "-Dspring.datasource.url=${DB_URL}", \
  "-Dspring.datasource.username=${DB_USERNAME}", \
  "-Dspring.datasource.password=${DB_PASSWORD}", \
  "-Dserver.port=${SERVER_PORT}", \
  "-jar", "/app/app.jar"]