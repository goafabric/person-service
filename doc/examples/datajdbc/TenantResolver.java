package org.goafabric.personservice.persistence.extensions;

import org.flywaydb.core.Flyway;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.annotation.RegisterReflection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Component
@RegisterReflection(classes = {org.flywaydb.core.internal.publishing.PublishingConfigurationExtension.class, org.flywaydb.database.postgresql.TransactionalModel.class}, memberCategories = {MemberCategory.INVOKE_PUBLIC_METHODS, MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS})
@ConditionalOnExpression("#{!('${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
@SuppressWarnings("java:S2095") //connection closing is handled by framework
public class TenantResolver  {

    private final String schemaPrefix;
    private final String defaultSchema;

    public TenantResolver(
                          @Value("${multi-tenancy.default-schema:PUBLIC}") String defaultSchema,
                          @Value("${multi-tenancy.schema-prefix:_}") String schemaPrefix) {
        this.defaultSchema = defaultSchema;
        this.schemaPrefix = schemaPrefix;
    }


    @Bean
    public FlywayMigrationStrategy flywayMigrationStrategy() {
        return flyway -> {};
    }

    @Bean
    public Boolean schemaCreator(Flyway flyway,
                                           @Value("${database.provisioning.goals}") String goals,
                                           @Value("${multi-tenancy.tenants}") String tenants,
                                           @Value("${multi-tenancy.schema-prefix:_}") String schemaPrefix) {
        if (goals.contains("-migrate")) {
            Arrays.asList(tenants.split(",")).forEach(tenant ->
                    Flyway.configure().configuration(flyway.getConfiguration())
                    .schemas(schemaPrefix + tenant).defaultSchema(schemaPrefix + tenant)
                    .placeholders(Map.of("tenantId", tenant))
                    .load().migrate()
            );

            Flyway.configure().configuration(flyway.getConfiguration())
                    .load().migrate();
        }
        return true;
    }


}