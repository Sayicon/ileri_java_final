import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const errorRate = new Rate('errors');
const latency   = new Trend('latency_over_time');

// 30 VU for 30 minutes — detects memory leaks, connection-pool exhaustion.
export const options = {
    stages: [
        { duration: '2m',  target: 30 }, // ramp-up
        { duration: '26m', target: 30 }, // soak
        { duration: '2m',  target: 0  }, // ramp-down
    ],
    thresholds: {
        http_req_duration: ['p(95)<800'],
        errors:            ['rate<0.01'],
    },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

function login() {
    const res = http.post(`${BASE}/api/auth/login`,
        JSON.stringify({ username: 'user1', password: 'pass1' }),
        { headers: { 'Content-Type': 'application/json' } });
    return res.status === 200 ? res.json('token') : null;
}

export default function () {
    const token = login();
    if (!token) { errorRate.add(1); sleep(2); return; }
    const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    };

    sleep(1);

    const start = Date.now();
    const r = http.get(`${BASE}/api/events`, { headers });
    latency.add(Date.now() - start);

    check(r, { 'events 200': res => res.status === 200 });
    errorRate.add(r.status !== 200);

    sleep(2);
}
