#!/bin/bash
set -e

TIMEOUT=120
BASE_URL="http://localhost:8080"

echo "[FAZ-8 SMOKE] docker compose up --build -d ..."
docker compose up --build -d

echo "[FAZ-8 SMOKE] Servisler ayaga kalkiyor (${TIMEOUT}s bekleniyor)..."
sleep "${TIMEOUT}"

echo "[FAZ-8 SMOKE] Gateway health check..."
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/actuator/health")
if [ "${HTTP_STATUS}" != "200" ]; then
  echo "FAIL: /actuator/health HTTP ${HTTP_STATUS}"
  docker compose down
  exit 1
fi
echo "OK: /actuator/health -> 200"

echo "[FAZ-8 SMOKE] GET /api/events check..."
HTTP_STATUS=$(curl -s -o /dev/null -w "%{http_code}" "${BASE_URL}/api/events")
if [ "${HTTP_STATUS}" != "200" ]; then
  echo "FAIL: GET /api/events HTTP ${HTTP_STATUS}"
  docker compose down
  exit 1
fi
echo "OK: GET /api/events -> 200"

echo "[FAZ-8 SMOKE] docker compose down..."
docker compose down

echo "[FAZ-8 SMOKE] TUM KONTROLLER GECTI"
