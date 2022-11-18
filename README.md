#docker compose
go to /src/deploy/docker and do "./stack up"

#run jvm multi image
docker pull goafabric/person-service:3.0.0-RC2 && docker run --name person-service --rm -p50800:50800 goafabric/person-service:3.0.0-RC2

#run native image
docker pull goafabric/person-service-native:3.0.0-RC2 && docker run --name person-service-native --rm -p50800:50800 goafabric/person-service-native:3.0.0-RC2 -Xmx64m

#run native image arm
docker pull goafabric/person-service-native-arm64v8:3.0.0-RC2 && docker run --name person-service-native-arm64v8 --rm -p50800:50800 goafabric/person-service-native-arm64v8:3.0.0-RC2 -Xmx64m
