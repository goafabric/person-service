#docker compose
go to /src/deploy/docker and do "./stack up"

#run jvm multi image
docker pull goafabric/person-service:2.0.1-SNAPSHOT && docker run --name person-service --rm -p50900:50900 goafabric/person-service:2.0.1-SNAPSHOT

#run native image
docker pull goafabric/person-service-native:2.0.1-SNAPSHOT && docker run --name person-service-native --rm -p50900:50900 goafabric/person-service-native:2.0.1-SNAPSHOT -Xmx64m

#force amd64
docker pull goafabric/person-service:2.0.1-SNAPSHOT && docker run --platform linux/amd64 --name person-service --rm -p50900:50900 goafabric/person-service:2.0.1-SNAPSHOT
