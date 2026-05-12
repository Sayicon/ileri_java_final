# Teknoloji Yığını, Ortam & Rubrik

## Puanlama Haritası (Rubrik → Faz Eşleştirmesi)

Her rubrik maddesi en az bir fazda **somut çıktıyla** karşılanır. Faz sonunda bu tablo doğrulanır.

| Kriter | Puan | Karşılandığı Faz(lar) | Somut Çıktı |
|---|---|---|---|
| API & Back-end | 10 | Faz 2, 3, 4 | 4 mikroservisin REST endpoint'leri (Spring Boot) |
| Generic Yapılar | 10 | Faz 1 | `ApiResponse<T>`, `Repository<T, ID>`, `PagedResult<T>`, `Validator<T>`, generic koleksiyonlar |
| Custom GUI | 10 | Faz 5 | JavaFX salon haritası: Canvas üzerine çizilen koltuk grid'i, renkli durum (boş/dolu/seçili/locked), zoom/pan |
| JDBC & NoSQL | 10 | Faz 2 (JDBC), Faz 3 (Redis) | PostgreSQL JDBC repository + Redis (session, koltuk lock, rate limit) |
| SOLID & OOP | 10 | Tüm fazlar | Strategy (ödeme), Factory (notification), Repository, DTO/Entity ayrımı, DI |
| Hata Yönetimi | 5 | Faz 1, 4 | `@ControllerAdvice` + RFC 7807 Problem Details, 4xx/5xx ayrımı |
| Performans Testleri | 5 | Faz 9 | k6 + JMeter senaryoları, rapor PDF/Markdown |
| Analiz & Doküman | 5 | Faz 10 | README.md + Mermaid mimari + performans raporu |
| **ZORUNLU TOPLAM** | **65** | | |
| Mikroservis Mimarisi | +10 | Faz 2-4 | 4 izole service, REST üzerinden JSON haberleşme |
| Gateway | +5 | Faz 6 | Spring Cloud Gateway (Kong değil — Java native zorunluluğu) |
| Mobil GUI | +5 | Faz 7 | Android (native Java) uygulama, aynı API'ye konuşur |
| Test-Driven Geliştirme | +10 | Tüm fazlar (A bölümleri) | Her fazda **A: testler önce commit** → **B: uygulama**, git timestamp ile kanıt |
| Dockerize Sistem | +5 | Faz 8 | `docker-compose up` ile tüm stack (4 service + Gateway + Postgres + Redis) |
| **EK TOPLAM** | **35** | | |
| **GENEL TOPLAM** | **100** | | |

---

## Teknoloji Yığını

| Bileşen | Teknoloji | Gerekçe |
|---|---|---|
| Dil | Java 21 (LTS) | PDF "Java" diyor; 21 modern (records, pattern matching, virtual threads) |
| Build | Maven (multi-module) | Mikroservis monorepo için parent POM + 4 child + shared lib |
| Backend framework | Spring Boot 3.3.x | Industry standard, OOP/DI/SOLID için ideal, JDBC + Redis + Web + Validation hazır |
| API Gateway | Spring Cloud Gateway | Java native (Kong Lua/Go değil — PDF "tüm bileşenler Java" diyor) |
| Service Discovery | Statik config (gateway routes YAML) | Eureka overkill; 4 service için YAML yeterli, demo'da görünür |
| İlişkisel DB | PostgreSQL 16 | Tam JDBC desteği, Docker'da hafif |
| Veri erişimi (RDBMS) | **Saf JDBC** (Spring JdbcTemplate'siz `Connection` + `PreparedStatement`) | PDF "JDBC" diyor — JPA/Hibernate açık değil. Saf JDBC ile katman izolasyonu daha şeffaf, dersin öğretim hedefine uygun. |
| NoSQL | Redis 7 (Jedis client) | PDF örnek olarak Redis/MongoDB veriyor. Redis seçildi: session/lock/rate-limit için organik, demo'da TTL gösterilebilir. |
| Custom GUI | JavaFX 21 (OpenJFX) | Canvas API ile custom graphics (salon haritası) Swing'e göre daha temiz, CSS theming bonus |
| Mobil GUI | Android (native Java, **Kotlin yok**) | PDF "Android (Java) veya JavaFX/Gluon" diyor. Android native Java — Gluon mobile toolchain kırılgan. |
| HTTP client (mobil) | Retrofit2 + OkHttp + Gson | Standart Android stack, Java uyumlu |
| Test framework | JUnit 5 + Mockito + AssertJ | Modern Java test standardı |
| Integration test | Testcontainers (Postgres, Redis) | Gerçek DB ile entegrasyon testi; mock değil |
| API test | REST-assured | Endpoint testleri için akıcı DSL |
| Performans testi | k6 (primary) + JMeter (secondary) | k6 script-bazlı CI'da koşturulabilir; JMeter GUI ile demo'da gösterilebilir |
| Container | Docker + Docker Compose | Ek özellik şartı |
| API doc | Springdoc OpenAPI 3 (Swagger UI) | Her servis için /swagger-ui.html |
| Logging | SLF4J + Logback + JSON encoder | Mikroservis log'ları stdout'a JSON |
| Validation | Jakarta Bean Validation (Hibernate Validator) | DTO seviyesinde `@Valid`, otomatik 400 dönüşü |
| Versiyon kontrolü | Git + GitHub | PDF açıkça commit dengesi istiyor |
| CI (opsiyonel) | GitHub Actions | Test logları otomatik üretilir |

### Kütüphane Yasakları

- **JPA/Hibernate yasak.** PDF "JDBC" diyor. JPA otomatik repository oluşturursa JDBC katmanı görünmez kalır → puanı tehlikeye atar. Saf JDBC + manuel mapper.
- **Lombok kullanılır** (`@Getter`, `@Builder`, `@RequiredArgsConstructor`) — okunabilirlik için, OOP prensiplerini gizlemez (records ile birleştirilir).
- **Kotlin yasak** (PDF Java dışı dil sıfır not).
- **Auto-config "magic"** minimum tutulur — DI ve config dosyalarındaki manuel bean tanımları sunumda savunulabilir olmalı.

---

## Geliştirme Ortamı Stratejisi

**Tek ortam:** Kerem'in Lenovo RTX 3050 + Windows laptop. Mikroservis stack Docker Compose ile lokal koşar; Android emülatör de aynı makinede.

- **IDE:** IntelliJ IDEA Community (Java + Maven), Android Studio (mobil modül için ayrı pencere).
- **JDK:** Eclipse Temurin 21.
- **Docker Desktop:** Postgres + Redis + 4 service + Gateway tek `docker-compose up` ile.
- **GPU gereği yok.** Bu proje ML değil; RTX 3050 boş duracak.
- **CachyOS geçişi planı bu projeyi etkilemez** — Docker + Maven + JDK Linux'ta aynı çalışır. Ama proje süresince **Windows'ta kal**: Android Studio + IntelliJ + Docker kombinasyonu kararlı, ortam değişikliği riski sıfır kazanç sağlar.
