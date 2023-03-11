package org.goafabric.personservice.persistence.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.goafabric.personservice.crossfunctional.HttpInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@RegisterReflectionForBinding(AuditBean.AuditEvent.class)
/** A class that audits all registered entities with @EntityListeners and writes the Audit Entries to the database **/
public class AuditBean {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private enum DbOperation {
        CREATE, READ, UPDATE, DELETE
    }

    public static record AuditEvent (
        String id,
        String tenantId,
        String referenceId,
        String type,
        DbOperation operation,
        String createdBy,
        Date createdAt,
        String modifiedBy,
        Date   modifiedAt,
        String oldValue,
        String newValue
    ) {}

    interface AuditInserter {
        void insertAudit(AuditEvent auditEvent, Object object);
    }

    private AuditInserter auditInserter;

    public AuditBean(AuditInserter auditInserter) {
        this.auditInserter = auditInserter;
    }

    public void afterRead(Object object, String id) {
        insertAudit(DbOperation.READ, id, object, object);
    }

    public void afterCreate(Object object, String id) {
        insertAudit(DbOperation.CREATE, id, null, object);
    }

    public void afterUpdate(Object object, String id, Object oldObject) {
        insertAudit(DbOperation.UPDATE, id, oldObject, object);
    }

    public void afterDelete(Object object, String id) {
        insertAudit(DbOperation.DELETE, id, object, null);
    }

    private void insertAudit(final DbOperation operation, String referenceId, final Object oldObject, final Object newObject) {
        try {
            final AuditEvent auditEvent =
                createAuditEvent(operation, referenceId, oldObject, newObject);
            log.debug("New audit event :\n{}", auditEvent);
            auditInserter.insertAudit(auditEvent, oldObject != null ? oldObject : newObject);
        } catch (Exception e) {
            log.error("Error during audit:\n{}", e.getMessage(), e);
        }
    }
    
    private AuditEvent createAuditEvent(
            DbOperation dbOperation, String referenceId, final Object oldObject, final Object newObject) throws JsonProcessingException {
        final Date date = new Date(System.currentTimeMillis());
        return new AuditEvent(
                UUID.randomUUID().toString(),
                HttpInterceptor.getTenantId(),
                referenceId,
                newObject.getClass().getSimpleName(),
                dbOperation,
                (dbOperation == DbOperation.CREATE ? HttpInterceptor.getUserName() : null),
                (dbOperation == DbOperation.CREATE ? date : null),
                ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? HttpInterceptor.getUserName() : null),
                ((dbOperation == DbOperation.UPDATE || dbOperation == DbOperation.DELETE) ? date : null),
                (oldObject == null ? null : getJsonValue(oldObject)),
                (newObject == null ? null : getJsonValue(newObject))
        );
    }

    private String getJsonValue(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(object);
    }
}
