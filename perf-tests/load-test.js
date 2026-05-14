import http from 'k6/http';
import { check, sleep } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const errorRate    = new Rate('errors');
const reserveTime  = new Trend('reserve_duration');

export const options = {
    stages: [
        { duration: '1m', target: 50 },   // ramp-up
        { duration: '3m', target: 50 },   // sustain 50 VU
        { duration: '1m', target: 0  },   // ramp-down
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'],
        errors:            ['rate<0.01'],
    },
};

const BASE = __ENV.BASE_URL || 'http://localhost:8080';

function login() {
    const res = http.post(`${BASE}/api/auth/login`,
        JSON.stringify({ username: 'user1', password: 'pass1' }),
        { headers: { 'Content-Type': 'application/json' } });
    check(res, { 'login 200': r => r.status === 200 });
    return res.status === 200 ? res.json('token') : null;
}

export default function () {
    // 1. login
    const token = login();
    if (!token) { errorRate.add(1); return; }
    const headers = {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
    };

    sleep(0.5);

    // 2. list events
    const evRes = http.get(`${BASE}/api/events`, { headers });
    check(evRes, { 'events 200': r => r.status === 200 });
    errorRate.add(evRes.status !== 200);
    if (evRes.status !== 200) return;

    const events = evRes.json();
    if (!events.length) { sleep(1); return; }
    const eventId = events[0].id;

    sleep(0.5);

    // 3. view seats
    const sRes = http.get(`${BASE}/api/events/${eventId}/seats`, { headers });
    check(sRes, { 'seats 200': r => r.status === 200 });
    errorRate.add(sRes.status !== 200);

    sleep(0.5);

    // 4. reserve
    const seats = sRes.status === 200 ? sRes.json() : [];
    const available = seats.filter(s => s.status === 'AVAILABLE');
    if (available.length) {
        const start = Date.now();
        const rRes = http.post(`${BASE}/api/tickets/reserve`,
            JSON.stringify({ eventId, seatId: available[0].id }),
            { headers });
        reserveTime.add(Date.now() - start);
        check(rRes, { 'reserve 2xx': r => r.status >= 200 && r.status < 300 });
        errorRate.add(rRes.status < 200 || rRes.status >= 300);
    }

    sleep(1);
}
