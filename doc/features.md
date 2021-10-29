#Working
- Web, ExceptionHandler
- Lombok, Mapstruct

- Health, Prometheus
- Swagger

- Security

- DurationLogger Aspect

- JPA
- Bean Validation

- Tracing

- Auditing (not in native mode, https://github.com/spring-projects-experimental/spring-native/issues/949)
- Multi Tenancy 
- Jaspyt Database Encryption

#Not Working
- Flyway (https://github.com/spring-projects-experimental/spring-native/issues/778)
- Resilience (https://github.com/spring-projects-experimental/spring-native/issues/960)
- Rest Interface (https://github.com/spring-projects-experimental/spring-native/issues/955)

- Arm Builds (https://github.com/paketo-buildpacks/stacks/issues/51)

#build times apple silicon:
- not working

#build times github:
- console: 4m
- web: 8m
- jpa: 15m