package org.goafabric.personservice.repository.extensions;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
import org.goafabric.personservice.extensions.HttpInterceptor;
import org.goafabric.personservice.repository.entity.AddressEo;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionListener;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@RegisterReflectionForBinding(UserNameAuditRevisionListener.class)
public class UserNameAuditRevisionListener implements RevisionListener {
    //TODO: orgunit needs to be handled differently on envers delete, otherwise it will be nulled, could be part of this CustomListener
    //Gradle: implementation("org.hibernate.orm:hibernate-envers") Plugin: id("org.hibernate.orm") version "6.3.1.Final" +  hibernate { enhancement { lazyInitialization(true) } }

    @Override
    public void newRevision(Object revisionEntity) {
        ((MyRevision) revisionEntity).setUserName(HttpInterceptor.getUserName());
    }

    @Entity @Table(name = "username_revision")
    @RevisionEntity(UserNameAuditRevisionListener.class)
    static class MyRevision extends DefaultRevisionEntity {
        private String userName;
        public String getUserName() { return userName; }
        public void setUserName(String userName) { this.userName = userName; }
    }

    //just an example method to list audit + metadata, encapsulated in object[3] .. yuck ....
    public static void listAuditData(EntityManagerFactory factory, String tenantId) {
        HttpInterceptor.setTenantId(tenantId);
        List<Object[]> objects = AuditReaderFactory.get(factory.createEntityManager()).createQuery()
                .forRevisionsOfEntity(AddressEo.class, false, true)
                .add(AuditEntity.revisionProperty("userName").eq("admin"))
                .getResultList();

        objects.forEach(object -> Arrays.stream(object).forEach(System.out::println));
    }
}