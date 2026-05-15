.PHONY: up down build logs ps clean smoke reset \
        rebuild-event rebuild-ticket rebuild-gateway rebuild-desktop \
        logs-event logs-ticket logs-gateway logs-auth logs-notification

# ── Temel ────────────────────────────────────────────────────────────────────

up:
	docker compose up -d

down:
	docker compose down

build:
	docker compose build

logs:
	docker compose logs -f

ps:
	docker compose ps

# Tüm container + volume + image sil, sıfırdan başlat
clean:
	docker compose down -v --rmi all

# Temizle + yeniden ayağa kaldır (sıfır başlangıç)
reset: clean up

# ── Seçili servis rebuild ─────────────────────────────────────────────────────

rebuild-event:
	docker compose up -d --build service-event

rebuild-ticket:
	docker compose up -d --build service-ticket

rebuild-gateway:
	docker compose up -d --build gateway

rebuild-desktop:
	docker compose up -d --build desktop-gui

# Bilet süresi dolma + koltuk rengi değişikliği için her ikisini birden yenile
rebuild-core:
	docker compose up -d --build service-event service-ticket

# ── Servis bazlı log ──────────────────────────────────────────────────────────

logs-event:
	docker compose logs -f service-event

logs-ticket:
	docker compose logs -f service-ticket

logs-gateway:
	docker compose logs -f gateway

logs-auth:
	docker compose logs -f service-auth

logs-notification:
	docker compose logs -f service-notification

# ── Test ──────────────────────────────────────────────────────────────────────

smoke:
	bash scripts/test_docker_compose_smoke.sh
