import http from "k6/http";
import { check, sleep } from 'k6';

export const options = {
    vus: 10, // Number of virtual users
    duration: '5s', // Duration of the test
};

export default function () {
  const res = http.get("http://localhost:50800/persons/findAll");
  check(res, { 'status was 200': (r) => r.status == 200 });
}

// https://k6.io/docs/get-started/running-k6/
// docker run --rm -i grafana/k6 run - <script.js