# dependency
implementation("org.javers:javers-spring-boot-starter-sql:7.8.0")

# annotation on Repositories 
@JaversSpringDataAuditable

# annotation on custom save methods (like saveAndFlush)
@JaversAuditable

# Authorprovider
@Bean
public AuthorProvider authorProvider() { return TenantContext::getUserName; }

# flyway
javers.sqlSchemaManagementEnabled: "false"
V3__javers.sql
