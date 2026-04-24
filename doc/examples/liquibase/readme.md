# dependency
implementation("org.liquibase:liquibase-core")

# properties
spring.liquibase.change-log: "classpath:db/changelog/changelog-root.xml"
database.provisioning.goals: "-import-demo-data"

