#docker compose
go to /src/deploy/docker and do "./stack up"

#run native image
docker pull goafabric/person-service-native:1.1.0 && docker run --name person-service-native --rm -p50800:50800 goafabric/person-service-native:1.1.0 -Xmx64m

#run amd image
docker pull goafabric/person-service:1.1.0 && docker run --name person-service-native --rm -p50800:50800 goafabric/person-service:1.1.0 -Xmx64m

#run arm image
docker pull goafabric/person-service-arm64v8:1.1.0 && docker run --name person-service-native --rm -p50800:50800 goafabric/person-service-arm64v8:1.1.0 -Xmx64m