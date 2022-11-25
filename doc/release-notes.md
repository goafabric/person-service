# 3.0.1

# 3.0.0
- upgrade to Spring Boot 3.0.0

# 2.0.1
- upgrade to Spring Boot 2.7.5
- upgrade to Spring Cloud 2021.0.4
- upgrade to openapi 1.6.12

- update jib plugin to version 3.3.0
- upgrade to ibm semeru baseimage 17.0.4

# 2.0.0
- Parity with Spring Boot Version

- owasp added to parent pom
- upgrade to Spring Boot 2.7.3
- upgrade to Spring Native 0.12.1

- Gradle build added

# 1.2.3
- upgrade to Spring Boot 2.7.1
- upgrade to Spring Cloud 2021.0.3
- upgrade to JDK 17.0.3

- Address 1:1 Example Relation added
- removed Jasypt (Database) Encryption => DB Encryption via Volume Encryption, Props via BASE64
- Multi Tenancy and Auditing both via TenantAware Base class => Apply to every Transaction Table
- AuditListener in Native mode is now working

# 1.2.2
- upgrade to Spring Boot 2.6.6
- upgrade to Spring Native 0.11.4
- upgrade to Lombok 1.18.22
- HttpInterceptor for Username and TenantId

# 1.2.1
- upgrade to Spring Boot 2.6.3
- upgrade to Spring Native 0.11.1
- Update to OpenAPI 1.6.4 + JIB 3.2.0

- Java 17 IBM Semeru Runtime for JVM Images
- Java 17 Compiler Level and Native Image
- Multi Arch JVM Images

# 1.2.0
- plugin management
- upgrade to Spring Native 0.11
- upgrade to Spring Boot 2.6.0
- upgrade to Spring Cloud 2021.0.0
- build updated to jdk 17

- OpenJ9HeapDump Endpoint removed, as it is now supported by Spring Boot 2.6.0

- Spring REST Variables now explicitly defined (e.g. @PathVariable("name")), as Name Inference seems to be removed from native 0.11
- Changes to SecurityConfiguration, as @ConditionalProperty seems to be resolved at build time now for native

# 1.1.0
- Upgrade to Spring Boot 2.5.3 / Spring Native 0.10.2
- Swagger added
- Jasypt Database Encryption added
- DurationLogger Aspect added

# 1.0.6
- spring sleuth for jaeger added

# 1.0.5
- upgrade to Spring Boot 2.5.2 and Spring Native 0.10.1
- @Transactional readded
- Auditing added (currently not working in native mode)

# 1.0.4
- Multi Tenancy added
- RestTemplate call added
- Upgrade to Spring Boot 2.5.1 and Spring Native 0.10.0                                                             
- Database Provisioning Toggle added

# 1.0.3
- sync with quarkus variant
- mongodb profile removed
- ExceptionHandler added

# 1.0.2
- version increased, and documentation updated
- prometheus metrics fix added

# 1.0.1
- native build enhanced
- mongodb profile added
- jpa image build is currently a little wonky, because of a problem with h2 and native images 

# 1.0.0
- initial version
