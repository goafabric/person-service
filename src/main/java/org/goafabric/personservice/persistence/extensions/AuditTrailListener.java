package org.goafabric.personservice.persistence.extensions;

import jakarta.persistence.*;
import org.goafabric.personservice.extensions.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;

// Simple Audittrail that fulfills the requirements of logging content changes + user + aot support, could be db independant
public class AuditTrailListener implements ApplicationContextAware {
    private static ApplicationContext context;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    enum DbOperation { CREATE, UPDATE, DELETE }

    @Entity
    @Table(name = "audit_trail")
    public static class AuditTrail {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private String id;
        private String organizationId;
        private String objectType;
        private String objectId;
        @Enumerated(EnumType.STRING)
        private DbOperation operation;
        private String createdBy;
        private LocalDateTime createdAt;
        private String modifiedBy;
        private LocalDateTime modifiedAt;
        private String oldValue;
        private String newValue;

        AuditTrail() {}
        public AuditTrail(String organizationId, String objectType, String objectId, DbOperation operation, String createdBy, LocalDateTime createdAt, String modifiedBy, LocalDateTime modifiedAt, String oldValue, String newValue) {
            this.organizationId = organizationId;
            this.objectType = objectType;
            this.objectId = objectId;
            this.operation = operation;
            this.createdBy = createdBy;
            this.createdAt = createdAt;
            this.modifiedBy = modifiedBy;
            this.modifiedAt = modifiedAt;
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        public String getOldValue() { return oldValue; }
        public String getNewValue() { return newValue; }

        @Override public String toString() { return "AuditTrail{id='%s', organizationId='%s', objectType='%s', objectId='%s', operation=%s, createdBy='%s', createdAt=%s, modifiedBy='%s', modifiedAt=%s, oldvalue='%s', newvalue='%s'}".formatted(id, organizationId, objectType, objectId, operation, createdBy, createdAt, modifiedBy, modifiedAt, oldValue, newValue); }
    }

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
                context.getBean(AuditDao.class).findOldObject(object.getClass(), id), object);
    }

    @PostRemove
    public void afterDelete(Object object) {
        insertAudit(DbOperation.DELETE, getId(object), object, null);
    }

    private void insertAudit(final DbOperation operation, String objectId, final Object oldObject, final Object newObject) {
        try {
            var auditTrail = createAuditTrail(operation, objectId, oldObject, newObject);
            log.debug("New audit:\n{}", auditTrail);
            context.getBean(AuditDao.class).insertAudit(auditTrail);
        } catch (Exception e) {
            log.error("Error during audit:\n{}", e.getMessage(), e);
        }
    }

    private AuditTrail createAuditTrail(
            DbOperation dbOperation, String objectId, final Object oldObject, final Object newObject) {
        return new AuditTrail(
                UserContext.getOrganizationId(),
                getTableName(newObject != null ? newObject : oldObject),
                objectId,
                dbOperation,
                (dbOperation == DbOperation.CREATE ? UserContext.getUserName() : null),
                (dbOperation == DbOperation.CREATE ? LocalDateTime.now() : null),
                ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? UserContext.getUserName() : null),
                ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? LocalDateTime.now() : null),
                (oldObject == null ? null : getJsonValue(oldObject)),
                (newObject == null ? null : getJsonValue(newObject))
        );
    }

    private String getJsonValue(final Object object)  {
        return JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    @Component
    @ConditionalOnExpression("#{!('${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
    static class AuditDao {
        @PersistenceContext
        private EntityManager entityManager;

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public <T> T findOldObject(Class<T> clazz, String id) { //new transaction helps us to retrieve the old value still inside the db
            var T = entityManager.find(clazz, id);
            return JSON_MAPPER.readValue(JSON_MAPPER.writeValueAsBytes(T), clazz);
        }

        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void insertAudit(AuditTrail auditTrail) {
            entityManager.persist(auditTrail);
        }
    }

    private static String getId(Object object) {
        return String.valueOf(context.getBean(EntityManagerFactory.class).getPersistenceUnitUtil().getIdentifier(object));
    }

    private static String getTableName(Object object) {
        return object != null ? object.getClass().getSimpleName().replace("Eo", "").toLowerCase() : "";
    }
}



