import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';

const errorRate = new Rate('errors');

// Sudden spike: 10 → 200 VU instantly, then back to 10.
export const options = {
    stages: [
        { duration: '1m',  target: 10  }, // baseline
        { duration: '10s', target: 200 }, // spike
        { duration: '3m',  target: 200 }, // sustain spike
        { duration: '10s', target: 10  }, // recover
        { duration: '2m',  target: 10  }, // verify recovery
        { duration: '30s', target: 0   },
    ],
    thresholds: {
        http_req_duration: ['p(95)<1000'],
        errors:            ['rate<0.05'],
    },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

export default function () {
    const r = http.get(`${BASE}/api/events`);
    check(r, { 'events 200': res => res.status === 200 });
    errorRate.add(r.status !== 200);
    sleep(0.2);
}
