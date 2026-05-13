# Klasör Yapısı

```
tbl324-event-ticketing/
├── AGENTS.md                          # bu dosya
├── README.md                          # final rapor (Faz 10'da doldurulur)
├── DECISIONS.md                       # tasarım kararları, savunma notları
├── docker-compose.yml                 # tüm stack (Faz 8)
├── pom.xml                            # parent POM (multi-module)
├── .gitignore
├── .github/workflows/                 # opsiyonel CI
│   └── tests.yml
│
├── shared/                            # ortak modül (DTO, generic sınıflar, exception base)
│   ├── pom.xml
│   └── src/main/java/com/tbl324/shared/
│       ├── api/
│       │   ├── ApiResponse.java       # Generic<T> wrapper
│       │   ├── PagedResult.java       # Generic<T> pagination
│       │   └── ProblemDetail.java     # RFC 7807
│       ├── exception/
│       │   ├── DomainException.java
│       │   ├── NotFoundException.java
│       │   ├── ConflictException.java
│       │   └── ValidationException.java
│       ├── repository/
│       │   └── Repository.java        # Generic<T, ID> base interface
│       └── validation/
│           └── Validator.java         # Generic<T> validator interface
│
├── service-auth/                      # 1. mikroservis: kullanıcı + oturum  ✅ Faz 3
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/tbl324/auth/
│       │   ├── AuthApplication.java
│       │   ├── config/
│       │   │   └── RedisConfig.java           # JedisPool (HikariCP benzeri pool)
│       │   ├── controller/
│       │   │   └── AuthController.java        # POST /auth/register, /auth/login, /auth/logout
│       │   ├── service/
│       │   │   ├── AuthService.java           # register/login/logout iş mantığı
│       │   │   └── SessionRedisRepository.java # session:{id}→userId, revoked:{id} denylist
│       │   ├── security/
│       │   │   ├── PasswordHasher.java        # BCryptPasswordEncoder wrapper
│       │   │   └── TokenService.java          # JJWT 0.12 — generate/parse/expire/sessionId
│       │   ├── filter/
│       │   │   └── JwtAuthFilter.java         # OncePerRequestFilter — /auth/login,/register hariç
│       │   ├── repository/
│       │   │   ├── BaseJdbcRepository.java    # Faz 2 Template Method kopyası
│       │   │   └── UserJdbcRepository.java    # findByUsername, existsByUsername/Email
│       │   ├── domain/
│       │   │   ├── User.java                  # immutable, explicit Builder
│       │   │   └── UserRole.java              # enum: USER, ADMIN
│       │   ├── dto/
│       │   │   ├── RegisterRequest.java       # @JsonCreator, @NotBlank, @Email, @Size
│       │   │   ├── LoginRequest.java
│       │   │   └── LoginResponse.java         # token, userId, username, role
│       │   └── exception/
│       │       └── GlobalExceptionHandler.java # 400/401/409/500
│       ├── main/resources/
│       │   ├── application.yml
│       │   └── db/migration/
│       │       ├── V1__init.sql               # roles, users tabloları
│       │       └── V2__seed.sql               # USER/ADMIN rolleri seed
│       └── test/java/com/tbl324/auth/
│           ├── ApplicationContextTest.java    # Testcontainers PG+Redis context yükü
│           ├── controller/
│           │   └── AuthControllerTest.java    # register/login/logout/409/401 — 6 test
│           ├── security/
│           │   ├── PasswordHasherTest.java    # 4 unit test
│           │   └── TokenServiceTest.java      # 6 unit test
│           └── service/
│               └── SessionRedisRepositoryTest.java # 4 test (Testcontainers Redis)
│
├── service-event/                     # 2. mikroservis: etkinlik + salon + koltuk  ✅ Faz 2
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/tbl324/event/
│       │   ├── EventApplication.java
│       │   ├── controller/
│       │   │   └── EventController.java       # GET/POST/PUT/DELETE /events, GET /events/{id}/seats
│       │   ├── service/
│       │   │   └── EventService.java          # findAll(page,size), findById, create, update, delete, findSeatsByEventId
│       │   ├── repository/
│       │   │   ├── BaseJdbcRepository.java    # abstract Template Method — saf JDBC (Connection+PreparedStatement)
│       │   │   ├── EventJdbcRepository.java
│       │   │   ├── VenueJdbcRepository.java
│       │   │   └── SeatJdbcRepository.java
│       │   ├── domain/
│       │   │   ├── Event.java                 # immutable, explicit Builder (Lombok yok)
│       │   │   ├── Venue.java
│       │   │   ├── Seat.java
│       │   │   ├── EventStatus.java           # ACTIVE, CANCELLED, COMPLETED
│       │   │   └── SeatStatus.java            # AVAILABLE, RESERVED, SOLD
│       │   ├── dto/
│       │   │   ├── EventDTO.java
│       │   │   ├── CreateEventRequest.java    # @NotBlank, @NotNull, @Future validations
│       │   │   ├── SeatDTO.java
│       │   │   └── VenueDTO.java
│       │   ├── mapper/
│       │   │   └── EventMapper.java           # static utility: toDTO(Event/Seat/Venue), toEntity(CreateEventRequest)
│       │   └── exception/
│       │       └── GlobalExceptionHandler.java # @RestControllerAdvice, RFC 7807 ProblemDetail
│       ├── main/resources/
│       │   ├── application.yml
│       │   └── db/migration/
│       │       ├── V1__init.sql               # venues, events, seats tabloları
│       │       └── V2__seed.sql               # 2 salon, 2 etkinlik, 500 koltuk (generate_series)
│       └── test/java/com/tbl324/event/
│           ├── DockerHostExtension.java
│           ├── controller/
│           │   └── EventControllerTest.java   # @WebMvcTest + @MockBean — 8 test
│           ├── exception/
│           │   └── GlobalExceptionHandlerTest.java  # 4 test (404/409/400/500)
│           └── repository/
│               └── EventRepositoryTest.java   # @Testcontainers PostgreSQL — 7 test
│
├── service-ticket/                    # 3. mikroservis: bilet rezervasyonu + ödeme
│   └── (aynı yapı)
│       # özel: SeatLockService (Redis SETNX + TTL), PaymentStrategy
│
├── service-notification/              # 4. mikroservis: bildirim (email/SMS mock)
│   └── (aynı yapı)
│       # özel: NotificationFactory (email/sms/push strategy)
│
├── gateway/                           # Spring Cloud Gateway  ✅ Faz 6
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/java/com/tbl324/gateway/
│       │   ├── GatewayApplication.java
│       │   ├── config/
│       │   │   └── RateLimitConfig.java        # @Bean ipKeyResolver — IP bazlı rate limit
│       │   └── filter/
│       │       ├── AuthGatewayFilter.java      # GlobalFilter order=-1 — /api/tickets Bearer zorunlu
│       │       └── LoggingFilter.java          # GlobalFilter LOWEST_PRECEDENCE — method+path log
│       ├── main/resources/
│       │   └── application.yml                 # 4 route, RequestRateLimiter, globalcors
│       └── test/java/com/tbl324/gateway/
│           ├── filter/
│           │   └── AuthGatewayFilterTest.java  # 5 unit test (MockServerWebExchange+StepVerifier)
│           ├── GatewayIntegrationTest.java     # 4 test (@SpringBootTest + WebTestClient)
│           └── resources/
│               └── application.yml            # test: default-filters:[] Redis yok, route→9999
│
├── desktop-gui/                       # JavaFX masaüstü uygulaması (Custom GUI)
│   ├── pom.xml
│   └── src/main/java/com/tbl324/desktop/
│       ├── DesktopApp.java
│       ├── view/
│       │   ├── LoginView.java
│       │   ├── EventListView.java
│       │   ├── SeatMapView.java       # ⭐ Custom Canvas — koltuk grid çizimi
│       │   └── TicketView.java
│       ├── controller/                # JavaFX controller'lar
│       ├── api/                       # ApiClient (HttpClient + Jackson)
│       └── model/                     # UI model sınıfları
│
├── android-app/                       # Mobil GUI (Android native Java)
│   ├── build.gradle                   # Gradle (Android için zorunlu)
│   ├── app/src/main/
│   │   ├── java/com/tbl324/mobile/
│   │   │   ├── MainActivity.java
│   │   │   ├── activity/              # LoginActivity, EventListActivity, SeatMapActivity, TicketActivity
│   │   │   ├── api/                   # Retrofit ApiService
│   │   │   ├── model/                 # POJOs
│   │   │   └── view/SeatMapView.java  # custom View — Canvas üzerine koltuk grid (mobil için)
│   │   ├── res/layout/                # XML layout'lar
│   │   └── AndroidManifest.xml
│   └── app/src/test/                  # JUnit + Mockito (Android instrumentation gerekmiyor)
│
├── perf-tests/                        # Faz 9 — performans testleri
│   ├── k6/
│   │   ├── load-test.js               # normal yük
│   │   ├── stress-test.js             # kırılma noktası
│   │   ├── spike-test.js              # ani trafik artışı
│   │   └── soak-test.js               # uzun süreli yük
│   ├── jmeter/
│   │   └── event-ticketing.jmx        # GUI demo için
│   └── reports/                       # k6 JSON çıktıları → markdown tabloya çevrilir
│
├── test-logs/                         # her faz sonunda Maven test çıktısı
│   ├── faz-0-red.txt
│   ├── faz-0-green.txt
│   └── ...
│
├── docs/
│   ├── plan/                          # Proje planlama dokümanları (agent bağlamı için)
│   │   ├── tech-stack-and-rubric.md   # Teknoloji yığını, yasaklar, ortam, rubrik
│   │   ├── folder-structure.md        # Bu dosya — klasör yapısı
│   │   ├── risks.md                   # Risk listesi & mitigations
│   │   ├── phase-details-backup.md    # Tüm faz detaylarının yedeği (orijinal)
│   │   └── ileri-java-proje.pdf       # Dersin orijinal ödev tanımı (TBL324)
│   ├── architecture.md                # Mermaid component + sequence diagram'ları
│   ├── api/                           # Springdoc OpenAPI ihraç dosyaları
│   └── performance-report.md          # Faz 9 çıktısı
│
└── scripts/
    ├── seed-data.sql                  # demo verisi (etkinlik, salon, kullanıcı)
    ├── wait-for-it.sh                 # docker-compose health check
    └── run-perf.sh                    # k6 + report üretimi
```
