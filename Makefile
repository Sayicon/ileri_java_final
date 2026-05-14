.PHONY: up down build logs ps clean smoke

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

clean:
	docker compose down -v --rmi all

smoke:
	bash scripts/test_docker_compose_smoke.sh
