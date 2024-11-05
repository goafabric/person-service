package org.goafabric.personservice.persistence.extensions;

import jakarta.persistence.EntityManager;
import org.goafabric.personservice.extensions.TenantContext;
import org.hibernate.Session;
import org.hibernate.jdbc.Work;
import org.javers.repository.sql.ConnectionProvider;
import org.javers.spring.auditable.AuthorProvider;
import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

@Configuration
@ImportRuntimeHints(JaversNativeConfiguration.ApplicationRuntimeHints.class)
public class JaversNativeConfiguration {
    static class ApplicationRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {

            hints.resources().registerResourceBundle("org.aspectj.weaver.weaver-messages");

            addClass(hints, java.util.ArrayList.class);

            // org.javers.core.*
            addClass(hints, org.javers.core.Javers.class);
            addClass(hints, org.javers.core.JaversBuilder.class);
            addClass(hints, org.javers.core.JaversBuilderPlugin.class);
            addClass(hints, org.javers.core.JaversCore.class);
            addClass(hints, org.javers.core.JaversCoreProperties.class);
            addClassWithInner(hints, "org.javers.core.JaversCoreProperties$PrettyPrintDateFormats");

            // org.javers.core.commit.*
            addClass(hints, org.javers.core.commit.Commit.class);
            addClass(hints, org.javers.core.commit.CommitFactory.class);
            addClass(hints, org.javers.core.commit.CommitId.class);
            addClass(hints, org.javers.core.commit.CommitIdFactory.class);
            addClass(hints, org.javers.core.commit.CommitSeqGenerator.class);
            addClass(hints, org.javers.core.commit.DistributedCommitSeqGenerator.class);

            // org.javers.core.diff.*
            addClass(hints, org.javers.core.diff.DiffFactory.class);
            addClass(hints, org.javers.core.diff.appenders.ArrayChangeAppender.class);
            addClass(hints, org.javers.core.diff.appenders.CollectionAsListChangeAppender.class);
            addClass(hints, org.javers.core.diff.appenders.MapChangeAppender.class);
            addClass(hints, org.javers.core.diff.appenders.NewObjectAppender.class);
            addClass(hints, org.javers.core.diff.appenders.ObjectRemovedAppender.class);
            addClass(hints, org.javers.core.diff.appenders.OptionalChangeAppender.class);
            addClass(hints, org.javers.core.diff.appenders.ReferenceChangeAppender.class);
            addClass(hints, org.javers.core.diff.appenders.SetChangeAppender.class);
            addClass(hints, org.javers.core.diff.appenders.SimpleListChangeAppender.class);
            addClass(hints, org.javers.core.diff.appenders.ValueChangeAppender.class);

            // org.javers.core.graph.*
            addClass(hints, org.javers.core.graph.CollectionsCdoFactory.class);
            addClass(hints, org.javers.core.graph.LiveCdoFactory.class);
            addClass(hints, org.javers.core.graph.LiveGraphFactory.class);
            addClass(hints, org.javers.core.graph.ObjectAccessHook.class);
            addClass(hints, org.javers.core.graph.ObjectHasher.class);
            addClass(hints, org.javers.core.graph.SnapshotObjectHasher.class);
            addClass(hints, org.javers.core.graph.TailoredJaversFieldFactory.class);

            // org.javers.core.json.*
            addClass(hints, org.javers.core.json.JsonConverter.class);
            addClass(hints, org.javers.core.json.JsonConverterBuilder.class);

            // org.javers.core.json.typeadapter.change.*
            addClass(hints, org.javers.core.json.typeadapter.change.ArrayChangeTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.change.ChangeTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.change.ListChangeTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.change.MapChangeTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.change.NewObjectTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.change.ObjectRemovedTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.change.ReferenceChangeTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.change.SetChangeTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.change.ValueChangeTypeAdapter.class);

            // org.javers.core.json.typeadapter.commit.*
            addClass(hints, org.javers.core.json.typeadapter.commit.CdoSnapshotStateTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.commit.CdoSnapshotTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.commit.CommitIdTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.commit.CommitMetadataTypeAdapter.class);
            addClass(hints, org.javers.core.json.typeadapter.commit.GlobalIdTypeAdapter.class);

            // org.javers.core.metamodel.*
            addClass(hints, org.javers.core.metamodel.object.CdoSnapshot.class);
            addClass(hints, org.javers.core.metamodel.object.GlobalId.class);
            addClass(hints, org.javers.core.metamodel.object.GlobalIdFactory.class);
            addClass(hints, org.javers.core.metamodel.scanner.AnnotationNamesProvider.class);
            addClass(hints, org.javers.core.metamodel.scanner.ClassAnnotationsScanner.class);
            addClass(hints, org.javers.core.metamodel.scanner.ClassScanner.class);
            addClass(hints, org.javers.core.metamodel.scanner.FieldBasedPropertyScanner.class);
            addClass(hints, org.javers.core.metamodel.type.EntityType.class);
            addClass(hints, org.javers.core.metamodel.type.ListType.class);
            addClass(hints, org.javers.core.metamodel.type.TypeMapper.class);

            // org.javers.core.snapshot.*
            addClass(hints, org.javers.core.snapshot.ChangedCdoSnapshotsFactory.class);
            addClass(hints, org.javers.core.snapshot.SnapshotDiffer.class);
            addClass(hints, org.javers.core.snapshot.SnapshotFactory.class);
            addClass(hints, org.javers.core.snapshot.SnapshotGraphFactory.class);

            // org.javers.guava.*
            addClass(hints, org.javers.guava.MultimapChangeAppender.class);
            addClass(hints, org.javers.guava.MultisetChangeAppender.class);

            // org.javers.hibernate.integration.*
            addClass(hints, org.javers.hibernate.integration.HibernateUnproxyObjectAccessHook.class);

            // org.javers.repository.*
            addClass(hints, org.javers.repository.api.JaversExtendedRepository.class);
            addClass(hints, org.javers.repository.api.JaversRepository.class);
            addClass(hints, org.javers.repository.api.QueryParams.class);
            addClass(hints, org.javers.repository.jql.ChangesQueryRunner.class);
            addClass(hints, org.javers.repository.jql.QueryCompiler.class);
            addClass(hints, org.javers.repository.jql.QueryRunner.class);
            addClass(hints, org.javers.repository.jql.ShadowQueryRunner.class);
            addClass(hints, org.javers.repository.jql.ShadowStreamQueryRunner.class);
            addClass(hints, org.javers.repository.jql.SnapshotQueryRunner.class);

            // org.javers.repository.sql.*
            addClass(hints, org.javers.repository.sql.ConnectionProvider.class);
            addClass(hints, org.javers.repository.sql.DialectName.class);
            addClass(hints, org.javers.repository.sql.JaversSqlRepository.class);
            addClass(hints, org.javers.repository.sql.SqlRepositoryBuilder.class);
            addClass(hints, org.javers.repository.sql.SqlRepositoryConfiguration.class);
            addClass(hints, org.javers.repository.sql.finders.CdoSnapshotFinder.class);
            addClass(hints, org.javers.repository.sql.finders.CommitPropertyFinder.class);
            addClass(hints, org.javers.repository.sql.repositories.CdoSnapshotRepository.class);
            addClass(hints, org.javers.repository.sql.repositories.CommitMetadataRepository.class);
            addClass(hints, org.javers.repository.sql.repositories.GlobalIdRepository.class);
            addClass(hints, org.javers.repository.sql.schema.FixedSchemaFactory.class);
            addClass(hints, org.javers.repository.sql.schema.JaversSchemaManager.class);
            addClass(hints, org.javers.repository.sql.schema.SchemaNameAware.class);
            addClass(hints, org.javers.repository.sql.schema.TableNameProvider.class);
            addClass(hints, org.javers.repository.sql.session.Session.class);

            // org.javers.shadow.*
            addClass(hints, org.javers.shadow.ShadowFactory.class);

            // org.javers.spring.*
            addClass(hints, org.javers.spring.JaversSpringProperties.class);
            addClass(hints, org.javers.spring.RegisterJsonTypeAdaptersPlugin.class);

            // org.javers.spring.annotation.*
            addClass(hints, org.javers.spring.annotation.JaversAuditable.class);
            addClass(hints, org.javers.spring.annotation.JaversAuditableConditionalDelete.class);
            addClass(hints, org.javers.spring.annotation.JaversAuditableDelete.class);
            addClass(hints, org.javers.spring.annotation.JaversSpringDataAuditable.class);

            // org.javers.spring.auditable.*
            addClass(hints, org.javers.spring.auditable.AuthorProvider.class);
            addClass(hints, org.javers.spring.auditable.CommitPropertiesProvider.class);

            // org.javers.spring.auditable.aspect.*
            addClass(hints, org.javers.spring.auditable.aspect.JaversAuditableAspect.class);
            addClass(hints, org.javers.spring.auditable.aspect.JaversCommitAdvice.class);

            // org.javers.spring.auditable.aspect.springdata.*
            addClass(hints, org.javers.spring.auditable.aspect.springdata.AbstractSpringAuditableRepositoryAspect.class);

            // org.javers.spring.boot.sql
            addClass(hints, org.javers.spring.boot.sql.JaversSqlAutoConfiguration.class);
            addClassWithInner(hints, "org.javers.spring.boot.sql.JaversSqlAutoConfiguration$$SpringCGLIB$$0");
            addClass(hints, org.javers.spring.boot.sql.JaversSqlProperties.class);
            addClass(hints, org.javers.spring.jpa.JpaHibernateConnectionProvider.class);

        }
    }


    private static void addClass(Hints hints, Class<?> clazz) {
        hints.reflection().registerType(clazz, builder ->
                builder.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS));
    }

    // Fallback for dynamically loading classes with "$$"
    private static void addClassWithInner(Hints hints, String className) {
        try {
            Class<?> clazz = Class.forName(className);
            hints.reflection().registerType(clazz, builder ->
                    builder.withMembers(MemberCategory.INVOKE_DECLARED_CONSTRUCTORS, MemberCategory.INVOKE_DECLARED_METHODS));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Class not found: " + className, e);
        }
    }

    @Bean //fix for NP inside JpaHibernateConnectionProvider
    public ConnectionProvider customConnectionProvider(EntityManager entityManager) {
        return () -> {
            var session = entityManager.unwrap(Session.class);
            var connectionWork = new GetConnectionWork();
            session.doWork(connectionWork);
            return connectionWork.theConnection;
        };
    }

    private class GetConnectionWork implements Work {
        private Connection theConnection;

        @Override
        public void execute(Connection connection) throws SQLException {
            theConnection = connection;
        }
    }

    @Bean
    public AuthorProvider authorProvider() {
        return TenantContext::getUserName;
    }
}
