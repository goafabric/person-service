# dependency
implementation("org.javers:javers-spring-boot-starter-sql:7.6.3")

# annotation on Repositories
@JaversSpringDataAuditable

# limitations
- currently only working with save and not saveAndFlush do to JaversSpringDataAuditableRepositoryAspect
- Multi Schema support has to be fiddled out, out of the box only publc schema