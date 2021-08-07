#!/bin/bash
git pull
time mvn clean install -P docker-image-native
docker pull goafabric/person-service-native:1.0.7-SNAPSHOT && docker run --name person-service-native --rm -p50800:50800 goafabric/person-service-native:1.0.7-SNAPSHOT -Xmx64m

