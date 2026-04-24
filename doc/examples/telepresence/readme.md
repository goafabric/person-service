# once
https://telepresence.io/docs/install/client

telepresence helm install

# start

telepresence connect -n example

telepresence intercept person-service-application --port 50800:8080

telepresence leave person-service-application

telepresence quit