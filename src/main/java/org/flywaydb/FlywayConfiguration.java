package org.flywaydb;

import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.*;

@Configuration
@NativeHint(trigger = org.flywaydb.core.Flyway.class,
        initialization = {@InitializationHint(types = {org.flywaydb.core.internal.util.FeatureDetector.class}, initTime = InitializationTime.BUILD)},
        types = {@TypeHint(types = {org.flywaydb.core.internal.logging.slf4j.Slf4jLogCreator.class, org.flywaydb.core.internal.logging.log4j2.Log4j2LogCreator.class}, access = {TypeAccess.DECLARED_CLASSES, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS})},
        resources = {@ResourceHint(patterns = {"org/flywaydb/core/internal/version.txt"})}
)
//also mind the --initialize-at-build-time inside parent pom
public class FlywayConfiguration {
}