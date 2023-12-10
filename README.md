![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

# docker compose
go to /src/deploy/docker and do "./stack up" or "./stack up -native"

# run jvm multi image
docker run --pull always --name person-service --rm -p50800:50800 goafabric/person-service:3.2.0

# run native image
docker run --pull always --name person-service-native --rm -p50800:50800 goafabric/person-service-native:3.2.0 -Xmx64m

# run native image arm
docker run --pull always --name person-service-native --rm -p50800:50800 goafabric/person-service-native-arm64v8:3.2.0 -Xmx64m

# loki logger
docker run --pull always --name person-service --rm -p50800:50800 --log-driver=loki --log-opt loki-url="http://host.docker.internal:3100/loki/api/v1/push" goafabric/person-service:3.2.0