import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

// Ramp from 10 → 500 VU to find the breaking point.
export const options = {
    stages: [
        { duration: '2m',  target: 10  },
        { duration: '2m',  target: 50  },
        { duration: '2m',  target: 100 },
        { duration: '2m',  target: 200 },
        { duration: '2m',  target: 300 },
        { duration: '2m',  target: 500 },
        { duration: '2m',  target: 500 }, // hold at peak
        { duration: '2m',  target: 0   }, // ramp-down
    ],
    thresholds: {
        // Mark as failure only if 99th percentile exceeds 2 s — we expect degradation.
        http_req_duration: ['p(99)<2000'],
    },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    // Lightweight: only event listing (read-only, no auth needed for stress probe)
    const r = http.get(`${BASE}/api/events`);
    check(r, { 'events ok': res => res.status === 200 });
    errorRate.add(r.status !== 200);
    sleep(0.1);
}
