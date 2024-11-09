# dependency
implementation("org.javers:javers-spring-boot-starter-sql:7.6.3")

# annotation on Repositories / disable existing one on entities
@JaversSpringDataAuditable

# annotation on saveAndFlush method (repository or logic)
@JaversAuditable
                                 
# flyway
javers.sqlSchemaManagementEnabled: "false"
V3__javers.sql
