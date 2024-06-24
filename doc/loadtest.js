// https://k6.io/docs/get-started/running-k6/

import http from "k6/http";
import { check, sleep } from 'k6';

export const options = {
    vus: 1, // Number of virtual users
    duration: '5s', // Duration of the test
};

const baseUrl = 'http://localhost:50800';

export default function () {
    checkResponse(http.get(`${baseUrl}/persons/findAll`));
    checkResponse(http.get(`${baseUrl}/persons/findByFirstName?firstName=Homer`));
    checkResponse(http.get(`${baseUrl}/persons/findByLastName?lastName=Simpson`));
    checkResponse(http.get(`${baseUrl}/persons/findByStreet?street=Evergreen%20Terrace`));
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
