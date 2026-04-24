docker run --rm --name hasura -p8081:8080 \
-e HASURA_GRAPHQL_DATABASE_URL=postgres://person-service:person-service@host.docker.internal:50810/person \
-e HASURA_GRAPHQL_ENABLE_CONSOLE=true \
hasura/graphql-engine:v2.36.11-ce