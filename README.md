# sonarqube
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=org.goafabric%3Aperson-service&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=org.goafabric%3Aperson-service)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=org.goafabric%3Aperson-service&metric=coverage)](https://sonarcloud.io/summary/new_code?id=org.goafabric%3Aperson-service)

[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=org.goafabric%3Aperson-service&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=org.goafabric%3Aperson-service)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.goafabric%3Aperson-service&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=org.goafabric%3Aperson-service)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=org.goafabric%3Aperson-service&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=org.goafabric%3Aperson-service)

# docker compose
go to /src/deploy/docker and do "./stack up" or "./stack up -native"

# run jvm multi image
docker run --pull always --name person-service --rm -p50800:50800 goafabric/person-service:$(grep '^version=' gradle.properties | cut -d'=' -f2)

# run native image
docker run --pull always --name person-service-native --rm -p50800:50800 goafabric/person-service-native:$(grep '^version=' gradle.properties | cut -d'=' -f2) -Xmx64m

# run native image arm
docker run --pull always --name person-service-native --rm -p50800:50800 goafabric/person-service-native-arm64v8:$(grep '^version=' gradle.properties | cut -d'=' -f2) -Xmx64m
