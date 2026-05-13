package com.tbl324.auth.controller;

import com.tbl324.auth.dto.LoginRequest;
import com.tbl324.auth.dto.RegisterRequest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
class AuthControllerTest {

    static {
        System.setProperty("DOCKER_HOST", "tcp://localhost:2375");
        System.setProperty("DOCKER_API_VERSION", "1.41");
    }

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Container
    static GenericContainer<?> redis =
            new GenericContainer<>("redis:7.2-alpine").withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("redis.host", redis::getHost);
        registry.add("redis.port", () -> redis.getMappedPort(6379));
    }

    @LocalServerPort
    int port;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        jdbcTemplate.execute("TRUNCATE TABLE users RESTART IDENTITY CASCADE");
    }

    @Test
    void register_validRequest_returns201WithToken() {
        given()
            .contentType(ContentType.JSON)
            .body(new RegisterRequest("kerem", "kerem@test.com", "pass1234"))
        .when()
            .post("/auth/register")
        .then()
            .statusCode(201)
            .body("token", notNullValue())
            .body("username", equalTo("kerem"));
    }

    @Test
    void register_duplicateUsername_returns409() {
        RegisterRequest req = new RegisterRequest("efe", "efe@test.com", "pass1234");

        given().contentType(ContentType.JSON).body(req).post("/auth/register");

        given()
            .contentType(ContentType.JSON)
            .body(new RegisterRequest("efe", "efe2@test.com", "pass1234"))
        .when()
            .post("/auth/register")
        .then()
            .statusCode(409);
    }

    @Test
    void login_correctCredentials_returns200WithToken() {
        given().contentType(ContentType.JSON)
               .body(new RegisterRequest("loginuser", "login@test.com", "mypass"))
               .post("/auth/register");

        given()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("loginuser", "mypass"))
        .when()
            .post("/auth/login")
        .then()
            .statusCode(200)
            .body("token", notNullValue());
    }

    @Test
    void login_wrongPassword_returns401() {
        given().contentType(ContentType.JSON)
               .body(new RegisterRequest("wrongpass", "wp@test.com", "correct"))
               .post("/auth/register");

        given()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("wrongpass", "incorrect"))
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401);
    }

    @Test
    void login_nonExistentUser_returns401() {
        given()
            .contentType(ContentType.JSON)
            .body(new LoginRequest("ghost", "pass"))
        .when()
            .post("/auth/login")
        .then()
            .statusCode(401);
    }

    @Test
    void logout_withValidToken_returns200() {
        String token = given()
            .contentType(ContentType.JSON)
            .body(new RegisterRequest("logoutuser", "lo@test.com", "pass1234"))
            .post("/auth/register")
            .jsonPath().getString("token");

        given()
            .header("Authorization", "Bearer " + token)
        .when()
            .post("/auth/logout")
        .then()
            .statusCode(200);
    }
}
