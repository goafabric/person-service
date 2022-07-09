package org.goafabric.personservice.crossfunctional;

import com.oracle.svm.core.annotate.Alias;
import com.oracle.svm.core.annotate.AutomaticFeature;
import com.oracle.svm.core.annotate.Substitute;
import com.oracle.svm.core.annotate.TargetClass;
import org.flywaydb.core.api.Location;
import org.flywaydb.core.api.resource.LoadableResource;
import org.flywaydb.core.internal.resource.classpath.ClassPathResource;
import org.flywaydb.core.internal.scanner.LocationScannerCache;
import org.flywaydb.core.internal.scanner.ResourceNameCache;
import org.flywaydb.core.internal.scanner.classpath.ResourceAndClassScanner;
import org.graalvm.nativeimage.hosted.Feature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.nativex.hint.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Configuration
@NativeHint(trigger = org.flywaydb.core.Flyway.class,
        initialization = {@InitializationHint(types = {org.flywaydb.core.internal.util.FeatureDetector.class}, initTime = InitializationTime.BUILD)},
        types = {@TypeHint(types = {org.flywaydb.core.internal.logging.slf4j.Slf4jLogCreator.class, org.flywaydb.core.internal.logging.log4j2.Log4j2LogCreator.class}, access = {TypeAccess.DECLARED_CLASSES, TypeAccess.DECLARED_CONSTRUCTORS, TypeAccess.PUBLIC_METHODS})},
        resources = {@ResourceHint(patterns = {"org/flywaydb/core/internal/version.txt"})}
)
//also mind the --initialize-at-build-time inside parent pom
public class FlywayNativeConfiguration {
}

@AutomaticFeature
final class FlywayFeature implements Feature {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayFeature.class);

    private static final String CLASSPATH_APPLICATION_MIGRATIONS_PROTOCOL = "classpath";
    private static final String JAR_APPLICATION_MIGRATIONS_PROTOCOL = "jar";
    private static final String FILE_APPLICATION_MIGRATIONS_PROTOCOL = "file";

    private static final String FLYWAY_LOCATIONS = "flyway.locations";
    private static final String DEFAULT_FLYWAY_LOCATIONS = "classpath:db/migration";

    @Override
    public void beforeAnalysis(BeforeAnalysisAccess access) {
        List<String> locations = Stream
                .of(System.getProperty(FLYWAY_LOCATIONS, DEFAULT_FLYWAY_LOCATIONS).split(",")).collect(Collectors.toList());

        try {
            List<String> migrations = discoverApplicationMigrations(locations);
            NativePathLocationScanner.setApplicationMigrationFiles(migrations);
        } catch (IOException | URISyntaxException e) {
            LOG.error("There was an error discovering the Flyway migrations: " + e.getMessage());
        }
    }

    private List<String> discoverApplicationMigrations(List<String> locations) throws IOException, URISyntaxException {
        List<String> applicationMigrationResources = new ArrayList<>();
        // Locations can be a comma separated list
        for (String location : locations) {
            // Strip any 'classpath:' protocol prefixes because they are assumed
            // but not recognized by ClassLoader.getResources()
            if (location != null && location.startsWith(CLASSPATH_APPLICATION_MIGRATIONS_PROTOCOL + ':')) {
                location = location.substring(CLASSPATH_APPLICATION_MIGRATIONS_PROTOCOL.length() + 1);
            }
            Enumeration<URL> migrations = Thread.currentThread().getContextClassLoader().getResources(location);
            while (migrations.hasMoreElements()) {
                URL path = migrations.nextElement();
                LOG.debug("Adding application migrations in path '" + path.getPath() + "' using protocol '" + path.getProtocol() + "'");
                final Set<String> applicationMigrations;
                if (JAR_APPLICATION_MIGRATIONS_PROTOCOL.equals(path.getProtocol())) {
                    try (FileSystem fileSystem = initFileSystem(path.toURI())) {
                        applicationMigrations = getApplicationMigrationsFromPath(location, path);
                    }
                } else if (FILE_APPLICATION_MIGRATIONS_PROTOCOL.equals(path.getProtocol())) {
                    applicationMigrations = getApplicationMigrationsFromPath(location, path);
                } else {
                    LOG.warn("Unsupported URL protocol '" + path.getProtocol() + "' for path '" + path.getPath() + "'. Migration files will not be discovered.");
                    applicationMigrations = null;
                }
                if (applicationMigrations != null) {
                    applicationMigrationResources.addAll(applicationMigrations);
                }
            }
        }
        return applicationMigrationResources;
    }

    private Set<String> getApplicationMigrationsFromPath(final String location, final URL path)
            throws IOException, URISyntaxException {
        try (Stream<Path> pathStream = Files.walk(Paths.get(path.toURI()))) {
            return pathStream.filter(Files::isRegularFile)
                    .map(it -> Paths.get(location, it.getFileName().toString()).toString())
                    // we don't want windows paths here since the paths are going to be used as classpath paths anyway
                    .map(it -> it.replace('\\', '/'))
                    .peek(it -> LOG.debug("Discovered path: " + it))
                    .collect(Collectors.toSet());
        }
    }

    private FileSystem initFileSystem(final URI uri) throws IOException {
        final Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        return FileSystems.newFileSystem(uri, env);
    }
}

@SuppressWarnings("rawtypes")
final class NativePathLocationScanner implements ResourceAndClassScanner {

    private static final Logger LOG = LoggerFactory.getLogger(NativePathLocationScanner.class);

    private static final String LOCATION_SEPARATOR = "/";
    private static List<String> applicationMigrationFiles;

    private final Collection<LoadableResource> scannedResources;

    public NativePathLocationScanner(Collection<Location> locations) {
        this.scannedResources = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        for (String migrationFile : applicationMigrationFiles) {
            if (canHandleMigrationFile(locations, migrationFile)) {
                LOG.debug("Loading " + migrationFile);
                scannedResources.add(new ClassPathResource(null, migrationFile, classLoader, StandardCharsets.UTF_8));
            }
        }
    }

    @Override
    public Collection<LoadableResource> scanForResources() {
        return scannedResources;
    }

    @Override
    public Collection<Class<?>> scanForClasses() {
        // Classes are not supported in native mode
        return Collections.emptyList();
    }

    public static void setApplicationMigrationFiles(List<String> applicationMigrationFiles) {
        NativePathLocationScanner.applicationMigrationFiles = applicationMigrationFiles;
    }

    private boolean canHandleMigrationFile(Collection<Location> locations, String migrationFile) {
        for (Location location : locations) {
            String locationPath = location.getPath();
            if (!locationPath.endsWith(LOCATION_SEPARATOR)) {
                locationPath += "/";
            }

            if (migrationFile.startsWith(locationPath)) {
                return true;
            } else {
                LOG.debug("Migration file '" + migrationFile + "' will be ignored because it does not start with '" + locationPath + "'");
            }
        }

        return false;
    }
}

@TargetClass(className = "org.flywaydb.core.internal.scanner.Scanner")
final class ScannerSubstitutions {

    @Alias
    private List<LoadableResource> resources = new ArrayList<>();

    @Alias
    private List<Class<?>> classes = new ArrayList<>();

    @Alias
    private HashMap<String, LoadableResource> relativeResourceMap = new HashMap<>();

    /**
     * Creates only {@link NativePathLocationScanner} instances.
     * Replaces the original method that tries to detect migrations using reflection techniques that are not allowed
     * in native mode.
     *
     * @see org.flywaydb.core.internal.scanner.Scanner#Scanner(Class, Collection, ClassLoader, Charset, boolean, boolean, ResourceNameCache, LocationScannerCache, boolean)
     */
    @Substitute
    public ScannerSubstitutions(
            Class<?> implementedInterface,
            Collection<Location> locations,
            ClassLoader classLoader,
            Charset encoding,
            boolean detectEncoding,
            boolean stream,
            ResourceNameCache resourceNameCache,
            LocationScannerCache locationScannerCache,
            boolean throwOnMissingLocations) {
        ResourceAndClassScanner scanner = new NativePathLocationScanner(locations);

        Collection resources = scanner.scanForResources();
        this.resources.addAll(resources);

        Collection scanForClasses = scanner.scanForClasses();
        classes.addAll(scanForClasses);

        for (LoadableResource resource : this.resources) {
            relativeResourceMap.put(resource.getRelativePath().toLowerCase(), resource);
        }
    }
}