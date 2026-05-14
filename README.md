# TBL324 Event Ticketing

Kocaeli Üniversitesi BSM TBL324 İleri Java Uygulamaları final projesi.

**Ekip:** Kerem Çekici · Efe  
**Teknoloji:** Java 21 · Spring Boot 3.3 · Spring Cloud Gateway · PostgreSQL · Redis · JavaFX · Android

> Detaylı mimari rapor Faz 10'da tamamlanacak.

---

## Hızlı Başlangıç

**Gereksinimler:** Docker 24+ · Docker Compose v2

```bash
# 1. Repo'yu klonla
git clone https://github.com/Sayicon/ileri_java_final.git
cd ileri_java_final

# 2. Ortam değişkenlerini ayarla
cp .env.example .env
# .env içindeki POSTGRES_PASSWORD ve JWT_SECRET değerlerini değiştir

# 3. Tüm stack'i başlat (ilk build ~5 dk sürer)
make build
make up

# 4. Hazır olduğunu doğrula
curl http://localhost:8080/actuator/health
# {"status":"UP"}

# 5. Etkinlikleri listele
curl http://localhost:8080/api/events
```

### Servis Portları

| Servis | Port | Açıklama |
|---|---|---|
| Gateway | 8080 | Tek dış giriş noktası |
| service-auth | 8081 | Dahili (network içi) |
| service-event | 8082 | Dahili |
| service-ticket | 8083 | Dahili |
| service-notification | 8084 | Dahili |

### Kullanışlı Komutlar

```bash
make logs    # Tüm servis logları (canlı)
make ps      # Çalışan container'lar
make down    # Stack'i durdur
make clean   # Container + volume + image temizle
make smoke   # Smoke test çalıştır
```
