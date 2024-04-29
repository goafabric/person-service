# Overview and Description concerning the Service                                               
[Overview](./OVERVIEW.MD)

# Docker Images

# docker compose
go to /src/deploy/docker and do "./stack up" or "./stack up -native"

# run jvm multi image
docker run --pull always --name person-service --rm -p50800:50800 goafabric/person-service:$(grep '^version=' gradle.properties | cut -d'=' -f2)

# run native image
docker run --pull always --name person-service-native --rm -p50800:50800 goafabric/person-service-native:$(grep '^version=' gradle.properties | cut -d'=' -f2) -Xmx64m

# run native image arm
docker run --pull always --name person-service-native --rm -p50800:50800 goafabric/person-service-native-arm64v8:$(grep '^version=' gradle.properties | cut -d'=' -f2) -Xmx64m