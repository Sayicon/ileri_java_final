FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY shared/pom.xml shared/
COPY service-ticket/pom.xml service-ticket/
RUN mvn -pl shared,service-ticket -am dependency:go-offline -q 2>/dev/null || true
COPY shared/src shared/src
COPY service-ticket/src service-ticket/src
RUN mvn -pl service-ticket -am -DskipTests package -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/service-ticket/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
