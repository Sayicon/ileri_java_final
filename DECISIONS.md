# DECISIONS.md — Tasarım Kararları & Savunma Notları

Sunumda her karar gerekçesiyle savunulacak.

| # | Karar Noktası | Bizim Seçimimiz | Savunma |
|---|---|---|---|
| 1 | Proje teması | Etkinlik bileti & salon yönetimi | Mikroservis sınırları doğal (Auth/Event/Ticket/Notification), custom GUI için canvas-doğal use case (koltuk haritası), Redis için organik kullanım (seat lock + session) |
| 2 | Veri erişimi | Saf JDBC (JPA yok) | PDF "JDBC" diyor; JPA otomatik repo magic'i JDBC katmanını gizler → rubrik puanı tehlikeye atar. Saf JDBC öğrenme hedefiyle birebir |
| 3 | NoSQL motoru | Redis (Mongo değil) | PDF her ikisini de örnek veriyor. Redis seçildi: session/lock/rate-limit için birincil rol, demo'da TTL/SETNX gösterilebilir; Mongo doc-store olarak zorlama olur |
| 4 | Mobil platform | Android native Java | PDF "Android (Java) veya JavaFX/Gluon" diyor. Gluon mobile NVIDIA/Windows toolchain'i kırılgan; Android Studio sağlam, RTX 3050'de risk yok |
| 5 | Gateway teknolojisi | Spring Cloud Gateway (Kong değil) | PDF "Kong vb." — vb. açık. Spring Cloud Gateway Java native, parent POM'a entegre. Kong (Lua/Go) Java zorunluluğunu zayıflatır |
| 6 | Servis sayısı | 4 (Auth, Event, Ticket, Notification) | 2 çok az (mikroservis gerekçesi zayıf), 6+ overengineering. 4 ile tek sorumluluk + cross-service çağrı (Ticket→Notification, Ticket→Event) doğal |
| 7 | Custom Graphics türü | Salon haritası (Canvas grid + zoom/pan) | "Standart bileşenler dışında" şartı: Canvas üzerine programatik çizim. Pie chart vb. de olur ama domain ile bağı yok |
| 8 | Build aracı | Maven multi-module (Gradle değil) | Spring Boot ekosistemi Maven-merkezli. Android tarafı Gradle (zorunlu) — iki build aracı paralel kullanılır |
| 9 | Auth mekanizması | JWT + Redis session denylist | Stateless JWT mikroservislerde standart. Redis denylist ile logout/revoke desteklenir |
| 10 | Test stratejisi | Unit (Mockito) + Integration (Testcontainers) + API (REST-assured) | TDD rubriği unit'ten fazlasını ima eder. Testcontainers ile gerçek Postgres/Redis — H2 mock leak'e açık |
| 11 | Ödeme | Mock PaymentStrategy (gerçek gateway yok) | Scope dışı; demo için yeterli. Strategy pattern için somut örnek |
| 12 | Bildirim | Mock (log'a yazar / Mailtrap stub) | Gerçek SMS/email scope dışı; Strategy + Factory pattern için somut örnek |
