# dependency
implementation("org.javers:javers-spring-boot-starter-sql:7.6.3")

# annotation on Repositories / also works for saveAndFlush on JPARepository
@JaversSpringDataAuditable

# Authorprovider
@Bean
public AuthorProvider authorProvider() { return TenantContext::getUserName; }

# flyway
javers.sqlSchemaManagementEnabled: "false"
V3__javers.sql
