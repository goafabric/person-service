// https://k6.io/docs/get-started/running-k6/

import http from "k6/http";
import { check, sleep } from 'k6';

export const options = {
    vus: 10, // Number of virtual users
    duration: '5s', // Duration of the test
};

const baseUrl = 'http://localhost:50800';

export default function () {
    checkResponse(http.get(`${baseUrl}/persons?page=0&size=3`));
    checkResponse(http.get(`${baseUrl}/persons?firstName=Homer&page=0&size=3`));
    checkResponse(http.get(`${baseUrl}/persons?lastName=Simpson&page=0&size=3`));
    //checkResponse(http.get(`${baseUrl}/persons/street?street=Evergreen Terrace&page=0&size=3`));
}

function checkResponse(response) {
    check(response, {
        'status is 200': (r) => r.status === 200,
    });
    if (response.status !== 200) {
        console.error(`Unexpected status for request: ${response.status}`);
        //console.error(response.body)
    }
}
