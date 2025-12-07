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

import java.util.Date;

// Simple Audittrail that fulfills the requirements of logging content changes + user + aot support, could be db independant
public class AuditTrailListener implements ApplicationContextAware {
    private static ApplicationContext context;

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final JsonMapper JSON_MAPPER = JsonMapper.builder().build();

    private enum DbOperation { CREATE, READ, UPDATE, DELETE }

    @Entity
    @Table(name = "audit_trail")
    @Access(AccessType.FIELD)
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
        private Date createdAt;
        private String modifiedBy;
        private Date modifiedAt;
        private String oldvalue;
        private String newvalue;

        public AuditTrail(String organizationId, String objectType, String objectId, DbOperation operation, String createdBy, Date createdAt, String modifiedBy, Date modifiedAt, String oldValue, String newValue) {
            this.organizationId = organizationId;
            this.objectType = objectType;
            this.objectId = objectId;
            this.operation = operation;
            this.createdBy = createdBy;
            this.createdAt = createdAt;
            this.modifiedBy = modifiedBy;
            this.modifiedAt = modifiedAt;
            this.oldvalue = oldValue;
            this.newvalue = newValue;
        }

        @Override
        public String toString() {
            return "AuditTrail{id='%s', organizationId='%s', objectType='%s', objectId='%s', operation=%s, createdBy='%s', createdAt=%s, modifiedBy='%s', modifiedAt=%s, oldvalue='%s', newvalue='%s'}"
                    .formatted(id, organizationId, objectType, objectId, operation, createdBy, createdAt, modifiedBy, modifiedAt, oldvalue, newvalue);
        }
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
                context.getBean(AuditLogic.class).findOldObject(object.getClass(), id), object);
    }

    @PostRemove
    public void afterDelete(Object object) {
        insertAudit(DbOperation.DELETE, getId(object), object, null);
    }

    private void insertAudit(final DbOperation operation, String referenceId, final Object oldObject, final Object newObject) {
        try {
            var auditTrail = createAuditTrail(operation, referenceId, oldObject, newObject);
            log.debug("New audit:\n{}", auditTrail);
            context.getBean(AuditLogic.class).insertAudit(auditTrail);
        } catch (Exception e) {
            log.error("Error during audit:\n{}", e.getMessage(), e);
        }
    }

    private AuditTrail createAuditTrail(
            DbOperation dbOperation, String referenceId, final Object oldObject, final Object newObject) {
        final Date date = new Date(System.currentTimeMillis());
        return new AuditTrail(
                UserContext.getOrganizationId(),
                getTableName(newObject != null ? newObject : oldObject),
                referenceId,
                dbOperation,
                (dbOperation == DbOperation.CREATE ? UserContext.getUserName() : null),
                (dbOperation == DbOperation.CREATE ? date : null),
                ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? UserContext.getUserName() : null),
                ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? date : null),
                (oldObject == null ? null : getJsonValue(oldObject)),
                (newObject == null ? null : getJsonValue(newObject))
        );
    }

    private String getJsonValue(final Object object)  {
        return JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }

    @Component
    @ConditionalOnExpression("#{!('${spring.autoconfigure.exclude:}'.contains('DataSourceAutoConfiguration'))}")
    static class AuditLogic {
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



