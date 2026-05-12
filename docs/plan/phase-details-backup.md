# Faz Detayları — Tam Yedek

> Bu dosya AGENTS.md'deki faz checklistlerinin **orijinal, tam halini** saklar.
> AGENTS.md'de tamamlanan fazlar özetlendikçe bu dosyaya başvurulabilir.
> Bu dosya hiçbir zaman kısaltılmaz/özetlenmez — kayıpsız arşiv olarak kalır.

---

## FAZ 0 — Proje İskeleti & Multi-Module Maven Kurulumu
**Sorumlu: Kerem + Efe (ortak kurulum, eşit commit)**
**Süre tahmini: 1 gün**

### A — Testler (önce commit'le)
- [ ] `shared/src/test/java/.../SmokeTest.java`: `assertTrue(true)` — Maven test pipeline çalışıyor mu?
- [ ] `service-auth/src/test/java/.../ApplicationContextTest.java`: Spring context başlıyor mu? (`@SpringBootTest` boş test).
- [ ] **Testleri commit'le.** Commit mesajı: `test(faz0): maven multi-module test pipeline sanity`.

### B — Uygulama
- [ ] GitHub repo oluştur: `tbl324-event-ticketing` (public veya private — kararı `DECISIONS.md`'ye yaz).
- [ ] Parent `pom.xml`: Java 21, Spring Boot 3.3.x, modules listesi (`shared`, `service-auth`, `service-event`, `service-ticket`, `service-notification`, `gateway`, `desktop-gui`). `android-app` ayrı (Gradle).
- [ ] `shared` modülü iskeleti: `ApiResponse<T>`, `PagedResult<T>`, `ProblemDetail` placeholder'ları (boş gövde, sadece sınıf imzaları).
- [ ] Her servis için minimum iskelet: `Application.java` + `application.yml` placeholder + `Dockerfile` placeholder.
- [ ] `.gitignore`: `target/`, `*.class`, `.idea/`, `.vscode/`, `*.iml`, `application-dev.yml`, `outputs/`, `node_modules/`, `build/`.
- [ ] `DECISIONS.md` oluştur: yukarıdaki "Kritik Tasarım Kararları" tablosunu kopyala. Tema savunması, JPA reddi, Kong reddi gerekçeleri burada.
- [ ] `README.md` placeholder: proje adı + 2 satır özet + "Detaylı rapor Faz 10'da" notu.
- [ ] `docs/architecture.md` placeholder: Mermaid component diagram (4 service + gateway + DB + Redis + 2 client).
- [ ] `mvn -pl shared test` ve `mvn -pl service-auth test` → ikisi de yeşil.
- [ ] Test çıktılarını `test-logs/faz-0-green.txt`'ye yaz.
- [ ] **AGENTS.md'yi güncelle** (Faz 0 done, tarihi yaz).

---

## FAZ 1 — `shared` Modülü: Generic Yapılar + Hata Modeli
**Sorumlu: Kerem (generic'lerle önceki `Response<T>` deneyimi var)**
**Süre tahmini: 2 gün**

### A — Testler (önce commit'le)
- [ ] `test_api_response.java`: `ApiResponse.success(data)` → `success=true`, `data` set, `error=null`.
- [ ] `test_api_response.java`: `ApiResponse.error(code, message)` → `success=false`, `error.code/message` set, `data=null`.
- [ ] `test_api_response.java`: Jackson ile serialize/deserialize → JSON round-trip lossless. Generic type token testi (`new TypeReference<ApiResponse<UserDTO>>(){}`).
- [ ] `test_paged_result.java`: `PagedResult.of(content, page, size, total)` → `hasNext`, `hasPrevious`, `totalPages` doğru hesaplanıyor.
- [ ] `test_paged_result.java`: Generic koleksiyon davranışı → `PagedResult<String>` ve `PagedResult<UserDTO>` aynı sınıf, farklı parametreleme.
- [ ] `test_validator.java`: Generic `Validator<T>` interface — `ValidationResult` `isValid`, `errors` field'ları; `validate(null)` anlamlı exception.
- [ ] `test_problem_detail.java`: RFC 7807 alan adları (`type`, `title`, `status`, `detail`, `instance`) eksiksiz; Jackson serialize "type"-as-URI.
- [ ] `test_domain_exception.java`: `DomainException` ve alt sınıfları (`NotFoundException`, `ConflictException`, `ValidationException`) doğru `errorCode` ve HTTP status taşıyor.
- [ ] `test_wildcards_demo.java`: **`? extends T` ve `? super T` kullanımı en az birer örnek.**
- [ ] **Testleri commit'le.** Mesaj: `test(faz1): generic API response, paged result, validator, problem detail`.

### B — Uygulama
- [ ] `shared/src/main/java/.../api/ApiResponse.java`: `<T>` generic, `static <T> success(T)`, `static <T> error(String code, String message)`. Lombok `@Getter` + `@Builder`.
- [ ] `shared/.../api/PagedResult.java`: `<T>` generic, immutable. `of(List<T>, int page, int size, long total)` factory.
- [ ] `shared/.../api/ProblemDetail.java`: RFC 7807 alanları + `errors` (validation için `Map<String, List<String>>`).
- [ ] `shared/.../validation/Validator.java`: `interface Validator<T> { ValidationResult validate(T input); }`. `ValidationResult` record (Java 17+).
- [ ] `shared/.../repository/Repository.java`: `interface Repository<T, ID> { Optional<T> findById(ID); PagedResult<T> findAll(int page, int size); T save(T); void delete(ID); }`. Default metotlar yok.
- [ ] `shared/.../exception/`: `DomainException` (abstract), `NotFoundException(404)`, `ConflictException(409)`, `ValidationException(400)`, `UnauthorizedException(401)`, `ForbiddenException(403)`, `RateLimitException(429)`.
- [ ] `shared/.../util/CollectionOps.java`: wildcards demo (`<T> void copyAll(List<? super T> dest, List<? extends T> src)`).
- [ ] `mvn -pl shared test` → tüm yeşil.
- [ ] Test çıktısı → `test-logs/faz-1-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 2 — `service-event`: JDBC Repository + Etkinlik CRUD
**Sorumlu: Efe (JDBC pattern'ı burada oturur, diğer servislere şablon olur)**
**Süre tahmini: 3 gün**

> **Neden Event ile başlanıyor?** Auth bağımlılığı olmayan tek servis (event listing public). JDBC pattern'ı burada oturduktan sonra diğer servislere kopyalanır.

### A — Testler (önce commit'le)
- [ ] `test_event_repository_jdbc.java`: **Testcontainers Postgres** ile gerçek bağlantı. `save(Event)` → `findById` doğru entity döner.
- [ ] `test_event_repository_jdbc.java`: `findAll(page, size)` → pagination doğru, `total` count doğru, sıralama deterministik (id asc).
- [ ] `test_event_repository_jdbc.java`: `delete(id)` → `findById` artık empty.
- [ ] `test_event_repository_jdbc.java`: `save` ile aynı external_code'a iki kayıt → DB-level unique constraint exception, Repository bunu `ConflictException`'a çevirir.
- [ ] `test_event_repository_jdbc.java`: SQL injection denemesi: `findByTitleLike("'; DROP TABLE events; --")` → PreparedStatement parametre binding ile zararsız, DB sağlam.
- [ ] `test_event_repository_jdbc.java`: `Connection` leak yok — 1000 ardışık çağrı sonrası HikariCP active connection sayısı baseline'a döner.
- [ ] `test_seat_map_repository.java`: Etkinliğe bağlı koltuk düzeni JDBC ile yükleniyor; salon kapasitesi doğru.
- [ ] `test_event_controller.java`: REST-assured ile `GET /events` → 200 + `ApiResponse<PagedResult<EventDTO>>` JSON şeması.
- [ ] `test_event_controller.java`: `GET /events/{id}` not found → 404 + ProblemDetail body, `type` URI, `status` 404.
- [ ] `test_event_controller.java`: `POST /events` validation hatası → 400 + `errors` field'ında detay.
- [ ] `test_error_handling.java`: `@ControllerAdvice` her `DomainException` alt tipini doğru HTTP status'a çeviriyor (404, 409, 400 için test).
- [ ] **Testleri commit'le.** Mesaj: `test(faz2): event service JDBC repository + REST controller + error handling`.

### B — Uygulama
- [ ] `service-event` `application.yml`: Postgres bağlantısı, Flyway path, HikariCP config.
- [ ] `service-event/.../db/migration/V1__init.sql`: `events`, `venues`, `seats` tabloları + index'ler + unique constraint'ler.
- [ ] `service-event/.../db/migration/V2__seed.sql`: 3 demo venue, 5 demo event, koltuk düzeni.
- [ ] `service-event/.../domain/Event.java`, `Venue.java`, `Seat.java`: domain modelleri.
- [ ] `service-event/.../repository/BaseJdbcRepository.java`: **abstract template** — ortak `getConnection`, `executeQuery`, `executeUpdate`.
- [ ] `service-event/.../repository/EventJdbcRepository.java implements Repository<Event, Long>`: tam CRUD + `findByTitleLike`.
- [ ] `service-event/.../repository/VenueJdbcRepository.java`, `SeatJdbcRepository.java`: aynı pattern.
- [ ] `service-event/.../service/EventService.java`: business logic, DI ile repository. `@Transactional` örnek.
- [ ] `service-event/.../controller/EventController.java`: `GET /events`, `GET /events/{id}`, `POST /events`, `PUT /events/{id}`, `DELETE /events/{id}`, `GET /events/{id}/seats`.
- [ ] `service-event/.../config/GlobalExceptionHandler.java`: `@ControllerAdvice`.
- [ ] `service-event/.../dto/`: `EventDTO`, `CreateEventRequest`, `UpdateEventRequest`, `SeatDTO`.
- [ ] `service-event/.../mapper/EventMapper.java`: Entity ↔ DTO dönüşüm (manuel).
- [ ] Springdoc OpenAPI bağımlılığı + `/swagger-ui.html` erişilebilir.
- [ ] `mvn -pl service-event test` → tüm yeşil.
- [ ] Test çıktısı → `test-logs/faz-2-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 3 — `service-auth`: JWT + Redis Session
**Sorumlu: Kerem**
**Süre tahmini: 2-3 gün**

### A — Testler (önce commit'le)
- [ ] `test_user_repository_jdbc.java`: `findByEmail` → case-insensitive arama, unique constraint.
- [ ] `test_password_hasher.java`: BCrypt — aynı password farklı salt → farklı hash; doğru password verify true, yanlış false.
- [ ] `test_token_service.java`: JWT üretim → header (HS256) + payload (sub, exp, iat) + signature. `parse(token)` round-trip doğru.
- [ ] `test_token_service.java`: Expired token → `parse` exception fırlatır.
- [ ] `test_token_service.java`: Tampered signature → exception.
- [ ] `test_session_redis_repository.java`: **Testcontainers Redis** — `save(sessionId, userId, ttl)` → `find` doğru userId döner.
- [ ] `test_session_redis_repository.java`: TTL geçince `find` empty (kısa TTL ile test, `Awaitility` ile bekle).
- [ ] `test_session_redis_repository.java`: `revoke(sessionId)` → denylist'e eklenir, `isRevoked` true.
- [ ] `test_auth_controller.java`: `POST /auth/register` → 201 + user oluştu, password DB'de hash'li (plaintext yok).
- [ ] `test_auth_controller.java`: `POST /auth/login` doğru kimlik → 200 + token; yanlış kimlik → 401.
- [ ] `test_auth_controller.java`: `POST /auth/logout` token ile → session revoke edildi, sonraki istek 401.
- [ ] `test_auth_controller.java`: Aynı email'le ikinci register → 409 Conflict.
- [ ] **Testleri commit'le.** Mesaj: `test(faz3): auth service JWT + redis session + password hashing`.

### B — Uygulama
- [ ] `service-auth/.../db/migration/V1__init.sql`: `users`, `roles`, `user_roles` tabloları.
- [ ] `service-auth/.../domain/User.java`, `Role.java`.
- [ ] `service-auth/.../repository/UserJdbcRepository.java implements Repository<User, Long>`.
- [ ] `service-auth/.../service/PasswordHasher.java`: BCrypt (`spring-security-crypto` minimum bağımlılığı).
- [ ] `service-auth/.../service/TokenService.java`: JJWT library ile JWT üretim/parse. Secret `${JWT_SECRET}` env.
- [ ] `service-auth/.../repository/SessionRedisRepository.java`: Jedis client. Key: `session:{sessionId}`, `revoked:{sessionId}`.
- [ ] `service-auth/.../service/AuthService.java`: register, login, logout, refresh.
- [ ] `service-auth/.../controller/AuthController.java`: `POST /auth/register`, `POST /auth/login`, `POST /auth/logout`, `GET /auth/me`.
- [ ] `service-auth/.../config/RedisConfig.java`: Jedis bean (pool config).
- [ ] `service-auth/.../filter/JwtAuthFilter.java`: Servlet filter — token parse → revocation kontrol → SecurityContext.
- [ ] `service-auth/.../config/GlobalExceptionHandler.java`.
- [ ] `mvn -pl service-auth test` → tüm yeşil.
- [ ] Test çıktısı → `test-logs/faz-3-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 4 — `service-ticket` + `service-notification`: Rezervasyon Akışı
**Sorumlu: Kerem (ticket) + Efe (notification)**
**Süre tahmini: 3-4 gün**

> **İş bölümü:** Kerem ticket service + seat lock + payment strategy; Efe notification service + factory + cross-service client'lar. Pair review zorunlu.

### A — Testler (önce commit'le)
- [ ] `test_seat_lock_service.java`: **Testcontainers Redis** — `tryLock(eventId, seatId, userId, ttl)` → SETNX, true döner.
- [ ] `test_seat_lock_service.java`: Aynı seat için ikinci `tryLock` farklı user → false.
- [ ] `test_seat_lock_service.java`: TTL geçince lock otomatik düşer, başka user `tryLock` → true.
- [ ] `test_seat_lock_service.java`: `release(eventId, seatId, userId)` → sadece sahibi release edebilir, başkası 403/exception.
- [ ] `test_ticket_repository_jdbc.java`: Bilet oluşturma → DB'de kayıt, koltuk durumu güncelleniyor.
- [ ] `test_ticket_repository_jdbc.java`: Concurrent rezervasyon — 100 thread aynı koltuğu rezerve etmeye çalışır → sadece 1 başarılı.
- [ ] `test_payment_strategy.java`: Strategy pattern — `MockPaymentStrategy` her zaman success; `FailingPaymentStrategy` her zaman fail.
- [ ] `test_payment_strategy.java`: Strategy değişikliği mevcut servisi etkilemez (Open/Closed kanıtı).
- [ ] `test_notification_factory.java`: Factory pattern — `NotificationFactory.create("email")` → `EmailNotifier`; `create("sms")` → `SmsNotifier`; bilinmeyen → exception.
- [ ] `test_notifier.java`: `MockEmailNotifier` log'a yazar, gerçek SMTP'ye gitmez.
- [ ] `test_ticket_controller.java`: `POST /tickets/reserve` happy path → 201 + ticket id, koltuk locked.
- [ ] `test_ticket_controller.java`: `POST /tickets/{id}/confirm` → ödeme Strategy çağrılır, başarılıysa 200.
- [ ] `test_ticket_controller.java`: `POST /tickets/{id}/confirm` ödeme fail → 402 Payment Required + lock release.
- [ ] `test_ticket_controller.java`: Reserve sonrası TTL içinde confirm edilmezse lock düşer (Awaitility).
- [ ] `test_cross_service.java`: Ticket service → Event service `GET /events/{id}` çağrısı.
- [ ] `test_cross_service.java`: Ticket service → Notification service çağrısı async — fire-and-log.
- [ ] **Testleri commit'le.** Mesaj: `test(faz4): ticket reservation + payment strategy + notification factory + seat locking`.

### B — Uygulama
- [ ] `service-ticket/.../db/migration/V1__init.sql`: `tickets`, `payments` tabloları + status enum.
- [ ] `service-ticket/.../domain/Ticket.java`, `Payment.java`.
- [ ] `service-ticket/.../service/SeatLockService.java`: Jedis `SETNX` + TTL. Lock key: `lock:seat:{eventId}:{seatId}`.
- [ ] `service-ticket/.../service/payment/PaymentStrategy.java` interface + `MockPaymentStrategy`, `WalletPaymentStrategy`.
- [ ] `service-ticket/.../service/TicketService.java`: reserve → lock → DB insert. confirm → payment → DB update → notification.
- [ ] `service-ticket/.../client/EventServiceClient.java`: Java `HttpClient` ile event service REST çağrısı.
- [ ] `service-ticket/.../client/NotificationServiceClient.java`: async fire (CompletableFuture, virtual thread executor).
- [ ] `service-ticket/.../controller/TicketController.java`: `POST /tickets/reserve`, `POST /tickets/{id}/confirm`, `POST /tickets/{id}/cancel`, `GET /tickets/me`.
- [ ] `service-notification/.../service/notifier/Notifier.java` interface + `EmailNotifier`, `SmsNotifier`, `PushNotifier`.
- [ ] `service-notification/.../service/NotificationFactory.java`: `Map<String, Notifier>` DI ile inject.
- [ ] `service-notification/.../controller/NotificationController.java`: `POST /notifications/send`, `GET /notifications/me`.
- [ ] `service-notification/.../repository/NotificationLogJdbcRepository.java`.
- [ ] `mvn test` (tüm modüller) → tüm yeşil.
- [ ] Test çıktısı → `test-logs/faz-4-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 5 — JavaFX Desktop GUI (Custom Graphics)
**Sorumlu: Efe**
**Süre tahmini: 3-4 gün**

> **Pragmatik test stratejisi:** Business logic (ApiClient, model dönüşümleri, koordinat hesaplamaları) saf Java unit test; UI render manuel demo + screenshot.

### A — Testler (önce commit'le)
- [ ] `test_api_client.java`: `ApiClient.login(email, pass)` → mock HTTP server, doğru JSON body gönderir.
- [ ] `test_api_client.java`: 401 yanıt → `UnauthorizedException`; 500 → `ServiceException`.
- [ ] `test_seat_grid_model.java`: `SeatGrid.fromSeats(List<SeatDTO>)` → 2D grid yapısı doğru oluşur.
- [ ] `test_seat_grid_model.java`: `SeatGrid.atPixel(x, y, scale)` → tıklanan pixel'e karşılık seat doğru bulunur.
- [ ] `test_seat_grid_model.java`: Boş satır/sütun olan salon → grid'de "spacer" hücre, tıklama no-op.
- [ ] `test_seat_color_mapper.java`: Seat status (AVAILABLE/LOCKED/SOLD/SELECTED) → farklı `Color`; deterministik.
- [ ] **Testleri commit'le.** Mesaj: `test(faz5): javafx api client + seat grid model + color mapper`.

### B — Uygulama
- [ ] `desktop-gui/pom.xml`: JavaFX 21 bağımlılığı.
- [ ] `desktop-gui/.../DesktopApp.java`: `Application.start()` entry.
- [ ] `desktop-gui/.../api/ApiClient.java`: `HttpClient` wrapper, token in-memory.
- [ ] `desktop-gui/.../view/LoginView.java`: FXML form.
- [ ] `desktop-gui/.../view/EventListView.java`: ListView + pagination.
- [ ] `desktop-gui/.../view/SeatMapView.java`: ⭐ **Custom Graphics**
  - JavaFX `Canvas` + `GraphicsContext`
  - Koltuk grid'i programatik çiziliyor (rectangle per seat, etiket)
  - Renkler: yeşil (boş), gri (satılmış), turuncu (locked), mavi (seçili)
  - Mouse: tıklama → seat seç, Ctrl+tıklama → multi-select, sürükle → pan, scroll → zoom
  - "Rezerve Et" → `POST /tickets/reserve`
- [ ] `desktop-gui/.../view/TicketView.java`: kullanıcının biletleri, QR placeholder.
- [ ] `desktop-gui/.../model/SeatGrid.java`.
- [ ] Manuel demo + screenshot'lar `docs/demo-screenshots/`.
- [ ] `mvn -pl desktop-gui test` → tüm yeşil.
- [ ] Test çıktısı → `test-logs/faz-5-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 6 — Spring Cloud Gateway
**Sorumlu: Kerem (Faz 5 ile paralel)**
**Süre tahmini: 1-2 gün**

### A — Testler (önce commit'le)
- [ ] `test_gateway_routing.java`: `GET /api/events` → service-event 8082'ye forward.
- [ ] `test_gateway_routing.java`: `POST /api/auth/login` → service-auth 8081'e forward.
- [ ] `test_gateway_routing.java`: Bilinmeyen path → 404.
- [ ] `test_gateway_rate_limit.java`: Aynı IP 100/sec → 429 + `Retry-After`.
- [ ] `test_gateway_auth.java`: `/api/tickets/**` → Authorization header yoksa 401.
- [ ] `test_gateway_circuit_breaker.java` (opsiyonel): Downstream down → 503 fallback.
- [ ] **Testleri commit'le.** Mesaj: `test(faz6): gateway routing + rate limit + auth filter`.

### B — Uygulama
- [ ] `gateway/pom.xml`: `spring-cloud-starter-gateway` + Redis reactive.
- [ ] `gateway/.../GatewayApplication.java`.
- [ ] `gateway/.../resources/application.yml`: route tanımları.
- [ ] Rate limit filter: Redis-backed, 100 req/sec global.
- [ ] Auth filter: token signature + revocation. Public path bypass.
- [ ] Logging filter: request id + latency.
- [ ] CORS config.
- [ ] `gateway/Dockerfile`.
- [ ] `mvn -pl gateway test` → tüm yeşil.
- [ ] Test çıktısı → `test-logs/faz-6-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 7 — Android Mobil GUI
**Sorumlu: Efe**
**Süre tahmini: 3-4 gün**

### A — Testler (önce commit'le)
- [ ] `android-app/.../test/ApiServiceTest.java`: Retrofit + MockWebServer ile login endpoint.
- [ ] `android-app/.../test/SeatViewModelTest.java`: seat seçim mantığı (max 5 koltuk, kapasite kontrol).
- [ ] `android-app/.../test/SeatGridModelTest.java`: SeatGrid model mantığı.
- [ ] **Testleri commit'le.** Mesaj: `test(faz7): android api service + seat view model`.

### B — Uygulama
- [ ] Android Studio'da yeni modül, Java (Kotlin değil!), min SDK 24, target SDK 34.
- [ ] `app/build.gradle`: Retrofit, OkHttp, Gson, AndroidX.
- [ ] `MainActivity.java` + `LoginActivity` + `EventListActivity` + `SeatMapActivity` + `TicketListActivity`.
- [ ] `api/ApiService.java`: Retrofit interface.
- [ ] `api/ApiClient.java`: Retrofit instance + token interceptor.
- [ ] `view/SeatMapView.java`: **Custom View** — `extends View`, `onDraw(Canvas)`.
- [ ] `res/layout/`: activity XML'leri.
- [ ] `AndroidManifest.xml`: INTERNET permission.
- [ ] Manuel test: Android emülatör, gateway `10.0.2.2:8080`.
- [ ] Demo screenshot'lar `docs/mobile-screenshots/`.
- [ ] `gradle test` → tüm yeşil.
- [ ] Test çıktısı → `test-logs/faz-7-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 8 — Dockerize: Tüm Stack Tek Komutla
**Sorumlu: Kerem**
**Süre tahmini: 1-2 gün**

### A — Testler (önce commit'le)
- [ ] `test_docker_compose_smoke.sh`: `docker compose up -d` → 60 sn → her servisin `/actuator/health` 200.
- [ ] `test_docker_compose_smoke.sh`: Gateway üzerinden `GET /api/events` → 200.
- [ ] `test_docker_compose_smoke.sh`: `docker compose down` temiz kapatma.
- [ ] **Testleri commit'le.** Mesaj: `test(faz8): docker compose smoke + health checks`.

### B — Uygulama
- [ ] Her servisin `Dockerfile`'ı: multi-stage build. Image boyutu < 250MB.
- [ ] `docker-compose.yml`:
  - `postgres:16.3-alpine` (multiple DB: auth_db, event_db, ticket_db, notification_db)
  - `redis:7.2-alpine` (persistence on)
  - 4 service + gateway (depends_on + healthcheck)
  - Network: tek bridge, sadece gateway dışa açık (8080)
- [ ] `application-docker.yml` profili: bağlantı string'leri service name kullanır.
- [ ] `.env` + `.env.example` (JWT_SECRET, DB password).
- [ ] `scripts/wait-for-it.sh`.
- [ ] `Makefile`: `make up`, `make down`, `make logs`.
- [ ] README'ye "Hızlı başlangıç" bölümü.
- [ ] Smoke script → yeşil.
- [ ] Test çıktısı → `test-logs/faz-8-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 9 — Performans Testleri (k6 + JMeter)
**Sorumlu: Efe (script) + Kerem (rapor analizi)**
**Süre tahmini: 2 gün**

### A — Test Senaryoları (commit'le)
- [ ] `perf-tests/k6/load-test.js`: 50 VU, 5 dakika, mixed workload (60% read, 20% reserve, 10% confirm, 10% auth).
- [ ] `perf-tests/k6/stress-test.js`: VU rampa (10→500), kırılma noktasını bul.
- [ ] `perf-tests/k6/spike-test.js`: 10 VU → 200 VU, recovery zamanı.
- [ ] `perf-tests/k6/soak-test.js`: 30 VU, 30 dakika. Memory leak kontrolü.
- [ ] `perf-tests/jmeter/event-ticketing.jmx`: JMeter HTML reporter.
- [ ] **Testleri commit'le.** Mesaj: `test(faz9): k6 load, stress, spike, soak + jmeter scenario`.

### B — Uygulama (test koşumu + rapor)
- [ ] `scripts/run-perf.sh`: 4 k6 senaryosu, JSON output'lar `perf-tests/reports/<timestamp>/`.
- [ ] Her senaryo için metrik tablo: avg/p50/p95/p99 latency, RPS, error rate.
- [ ] `docs/performance-report.md`:
  - Test ortamı (CPU, RAM, Docker stats)
  - Her senaryonun amacı + sonuçları
  - **Kırılma noktası analizi**
  - Profiling: en yavaş endpoint hangisi? Niye?
  - İyileştirme önerileri
- [ ] README'ye performans raporu linki.
- [ ] Test çıktısı → `test-logs/faz-9-green.txt`.
- [ ] **AGENTS.md'yi güncelle.**

---

## FAZ 10 — README + Mermaid + Son Kontroller
**Sorumlu: Kerem + Efe**
**Süre tahmini: 1-2 gün**

- [ ] `README.md` bölümleri:
  1. Proje özeti + amaç + ders bağlamı
  2. Mimari genel bakış + Mermaid component diagram
  3. Mermaid sequence diagram'lar (rezervasyon akışı, login akışı)
  4. Teknoloji yığını tablosu
  5. Hızlı başlangıç (`docker compose up`)
  6. API dokümantasyonu (Swagger UI URL'leri)
  7. Generic yapılar (kod örnekleriyle)
  8. Custom GUI (screenshot + canvas açıklaması)
  9. Mobil GUI screenshot
  10. Test stratejisi (TDD döngüsü, test-logs/ yapısı)
  11. Performans raporu özeti + link
  12. Tasarım kararları (DECISIONS.md özeti, 12 madde)
  13. Bilinen sınırlılıklar
  14. Geliştirme rehberi
- [ ] `docs/architecture.md` Mermaid diyagramlarını zenginleştir.
- [ ] Test logları kontrol: her fazın red + green log'u repoda var mı?
- [ ] Commit dağılımı: `git shortlog -sn` → Kerem ≈ Efe (±%20, NOT-2 için kritik). Min 40 commit toplam.
- [ ] Ekip listesi kontrol (NOT-4).
- [ ] Son sanity: `git clone` → `docker compose up` → manuel reserve+confirm akışı.
- [ ] Plagiarism kontrol.
- [ ] Sunum slide draft.
- [ ] **AGENTS.md'yi güncelle (proje tamamlandı).**
