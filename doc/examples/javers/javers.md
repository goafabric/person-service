# dependency
implementation("org.javers:javers-spring-boot-starter-sql:7.6.3")

# annotation on Repositories / disable existing one on entities
@JaversSpringDataAuditable
                                 
# flyway
javers.sqlSchemaManagementEnabled: "false"
V3__javers.sql

# limitations
- currently only working with save and not saveAndFlush do to JaversSpringDataAuditableRepositoryAspect
