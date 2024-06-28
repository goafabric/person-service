package org.goafabric.personservice.persistence.extensions;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.*;
import org.goafabric.personservice.extensions.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.Date;
import java.util.UUID;

// Simple Audittrail that fulfills the requirements of logging content changes + user + aot support, could be db independant
// Todo: for Multi Tenancy by discriminator wie need to change this to multiple tables instead of one, which means developers also have to create multiple flyway script tables
public class AuditTrailListener implements ApplicationContextAware {
    private static ApplicationContext context;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private enum DbOperation { CREATE, READ, UPDATE, DELETE }

    record AuditTrail(
            String id,
            String tenantId,
            String organizationId,
            String objectType,
            String objectId,
            DbOperation operation,
            String createdBy,
            Date createdAt,
            String modifiedBy,
            Date   modifiedAt,
            String oldValue,
            String newValue
    ) {}

    @Override
    @SuppressWarnings("java:S2696")
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @PostPersist
    public void afterCreate(Object object)  {
        insertAudit(DbOperation.CREATE,  getId(object), null, object);
    }

    @PostUpdate
    public void afterUpdate(Object object) {
        final String id = getId(object);
        insertAudit(DbOperation.UPDATE, id,
                context.getBean(AuditJpaUpdater.class).findOldObject(object.getClass(), id), object);
    }

    @PostRemove
    public void afterDelete(Object object) {
        insertAudit(DbOperation.DELETE, getId(object), object, null);
    }

    private void insertAudit(final DbOperation operation, String referenceId, final Object oldObject, final Object newObject) {
        try {
            var auditTrail = createAuditTrail(operation, referenceId, oldObject, newObject);
            log.debug("New audit:\n{}", auditTrail);
            context.getBean(AuditJpaInserter.class).insertAudit(auditTrail);
        } catch (Exception e) {
            log.error("Error during audit:\n{}", e.getMessage(), e);
        }
    }

    private AuditTrail createAuditTrail(
            DbOperation dbOperation, String referenceId, final Object oldObject, final Object newObject) throws JsonProcessingException {
        final Date date = new Date(System.currentTimeMillis());
        return new AuditTrail(
                UUID.randomUUID().toString(),
                TenantContext.getTenantId(),
                TenantContext.getOrganizationId(),
                getTableName(newObject != null ? newObject : oldObject),
                referenceId,
                dbOperation,
                (dbOperation == DbOperation.CREATE ? TenantContext.getUserName() : null),
                (dbOperation == DbOperation.CREATE ? date : null),
                ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? TenantContext.getUserName() : null),
                ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? date : null),
                (oldObject == null ? null : getJsonValue(oldObject)),
                (newObject == null ? null : getJsonValue(newObject))
        );
    }

    private String getJsonValue(final Object object) throws JsonProcessingException {
        return new ObjectMapper().registerModule(new JavaTimeModule()).writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    @Component
    @ConditionalOnExpression("#{!('${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
    static class AuditJpaUpdater {
        @PersistenceContext private EntityManager entityManager;

        @Transactional(propagation = Propagation.REQUIRES_NEW) //new transaction helps us to retrieve the old value still inside the db
        public <T> T findOldObject(Class<T> clazz, String id) {
            return entityManager.find(clazz, id);
        }
    }

    @Component
    @ConditionalOnExpression("#{!('${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
    @RegisterReflectionForBinding(AuditTrail.class)
    static class AuditJpaInserter {
        private final DataSource dataSource;
        private final String     schemaPrefix;

        public AuditJpaInserter(DataSource dataSource, @Value("${multi-tenancy.schema-prefix:_}") String schemaPrefix) {
            this.dataSource = dataSource;
            this.schemaPrefix = schemaPrefix;
        }

        public void insertAudit(AuditTrail auditTrail) { //we cannot use jpa because of the dynamic table name
            new SimpleJdbcInsert(dataSource)
                    .withTableName("audit_trail")
                    .execute(new BeanPropertySqlParameterSource(auditTrail));
        }
    }

    private static String getId(Object object) {
        return String.valueOf(context.getBean(EntityManagerFactory.class).getPersistenceUnitUtil().getIdentifier(object));
    }

    private static String getTableName(Object object) {
        return object.getClass().getSimpleName().replace("Eo", "").toLowerCase();
    }
}



