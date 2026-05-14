FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build
COPY pom.xml .
COPY shared/pom.xml shared/
COPY service-auth/pom.xml service-auth/
COPY service-event/pom.xml service-event/
COPY service-ticket/pom.xml service-ticket/
COPY service-notification/pom.xml service-notification/
COPY gateway/pom.xml gateway/
COPY desktop-gui/pom.xml desktop-gui/
RUN mvn -pl gateway -am dependency:go-offline -q 2>/dev/null || true
COPY gateway/src gateway/src
RUN mvn -pl gateway -DskipTests package -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /build/gateway/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
