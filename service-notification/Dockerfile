FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY shared/pom.xml shared/
COPY service-notification/pom.xml service-notification/
RUN mvn -pl shared,service-notification -am dependency:go-offline -q 2>/dev/null || true
COPY shared/src shared/src
COPY service-notification/src service-notification/src
RUN mvn -pl service-notification -am -DskipTests package -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/service-notification/target/*.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]
