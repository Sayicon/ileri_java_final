#!/usr/bin/env bash
# Run all k6 performance tests against the running stack.
# Usage: ./scripts/run-perf.sh [base_url]
# Requires: docker (k6 runs inside grafana/k6 image)

set -euo pipefail

BASE_URL="${1:-http://host-gateway:8080}"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
REPORT_DIR="perf-tests/reports/${TIMESTAMP}"

mkdir -p "${REPORT_DIR}"

echo "=== TBL324 Performance Tests — $(date) ==="
echo "Base URL : ${BASE_URL}"
echo "Report   : ${REPORT_DIR}"
echo

run_k6() {
    local name="$1"
    local script="perf-tests/${name}.js"
    local out="${REPORT_DIR}/${name}.json"
    echo "--- Running ${name} ---"
    docker run --rm \
        --add-host=host-gateway:host-gateway \
        -v "$(pwd)/perf-tests:/scripts" \
        -e BASE_URL="${BASE_URL}" \
        grafana/k6 run \
            --out "json=/scripts/reports/${TIMESTAMP}/${name}.json" \
            --summary-export "/scripts/reports/${TIMESTAMP}/${name}-summary.json" \
            "/scripts/${name}.js" 2>&1 | tee "${REPORT_DIR}/${name}.log"
    echo
}

run_k6 load-test
run_k6 stress-test
run_k6 spike-test
# soak-test is 30 min — uncomment for full run
# run_k6 soak-test

echo "=== All tests completed ==="
echo "Reports saved to: ${REPORT_DIR}"

# Produce a plain-text summary for test-logs/faz-9-green.txt
{
    echo "Faz 9 — Performans Testleri Özeti"
    echo "Tarih   : $(date)"
    echo "Base URL: ${BASE_URL}"
    echo
    for f in "${REPORT_DIR}"/*-summary.json; do
        name=$(basename "$f" -summary.json)
        echo "=== ${name} ==="
        # Extract key metrics if jq is available
        if command -v jq &>/dev/null; then
            jq -r '
                "  p95  : " + (.metrics.http_req_duration.values["p(95)" ] | tostring) + " ms",
                "  p99  : " + (.metrics.http_req_duration.values["p(99)" ] | tostring) + " ms",
                "  RPS  : " + (.metrics.http_reqs.values.rate             | tostring),
                "  Err% : " + ((.metrics.http_req_failed.values.rate*100) | tostring) + "%"
            ' "$f" 2>/dev/null || cat "$f"
        else
            cat "$f"
        fi
        echo
    done
} > "test-logs/faz-9-green.txt"

echo "Summary written to test-logs/faz-9-green.txt"
