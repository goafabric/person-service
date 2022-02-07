#!/bin/bash
git pull
time mvn clean deploy -P docker-image-native
#docker pull goafabric/person-service-native:1.2.1 && docker run --name person-service-native --rm -p50800:50800 goafabric/person-service-native:1.2.1 -Xmx64m

