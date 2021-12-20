#!/bin/bash
git pull
time mvn clean deploy -P docker-image-native
#docker pull goafabric/person-service-native:1.2.0 && docker run --name person-service-native --rm -p50800:50800 goafabric/person-service-native:1.2.0 -Xmx64m

