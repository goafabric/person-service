import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

val group: String by project
val version: String by project
java.sourceCompatibility = JavaVersion.VERSION_21

val dockerRegistry = "goafabric"
val nativeBuilder = "paketobuildpacks/java-native-image:9.8.0"
val baseImage = "ibm-semeru-runtimes:open-21.0.3_9-jre-focal@sha256:5cb19afa9ee0daeecb7c31be8253fecbbf6b5f6dcfb06883c41f045cb893bcec"

plugins {
	jacoco
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.graalvm.buildtools.native") version "0.10.3"
	id("com.google.cloud.tools.jib") version "3.4.4"
	id("net.researchgate.release") version "3.0.2"

	id("org.cyclonedx.bom") version "1.10.0"

	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.0"
	kotlin("plugin.jpa") version "2.1.0"
	kotlin("kapt") version "2.1.0"
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

//spring boot 3.4.0 native fix
dependencyManagement {
	dependencies {
		dependency("org.flywaydb:flyway-core:11.0.0")
		dependency("org.flywaydb:flyway-database-postgresql:11.0.0")
	}
}

dependencies {
	constraints {
		annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
		implementation("org.mapstruct:mapstruct:1.6.3")
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
		implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
		implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.6")
		implementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
		testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
	}
}

dependencies {
	//web
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")

	//monitoring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")

	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp")

	implementation("net.ttddyy.observation:datasource-micrometer-spring-boot")

	//openapi
	//spring boot 3.4.0 native fix
	//implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")
	implementation("io.projectreactor:reactor-core")

	//adapter
	implementation("io.github.resilience4j:resilience4j-spring-boot3") {exclude ("io.github.resilience4j", "resilience4j-micrometer")} // has to be excluded because of aot processor problem with kotlin
	implementation("org.springframework.boot:spring-boot-starter-aop")

	//code generation
	implementation("org.mapstruct:mapstruct")
	kapt("org.mapstruct:mapstruct-processor:1.6.3")

	//persistence
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa") {exclude("org.glassfish.jaxb", "jaxb-runtime")}
	implementation("com.h2database:h2")
	implementation("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	//mongodb
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	//test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.mockito.kotlin:mockito-kotlin")
	
	//kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	testImplementation("com.tngtech.archunit:archunit-junit5")
}

tasks.withType<Test> {
	useJUnitPlatform()
	exclude("**/*NRIT*")
	finalizedBy("jacocoTestReport")
}
tasks.jacocoTestReport { reports {csv.required.set(true) } }

jib {
	val amd64 = com.google.cloud.tools.jib.gradle.PlatformParameters(); amd64.os = "linux"; amd64.architecture = "amd64"; val arm64 = com.google.cloud.tools.jib.gradle.PlatformParameters(); arm64.os = "linux"; arm64.architecture = "arm64"
	from.image = baseImage
	to.image = "${dockerRegistry}/${project.name}:${project.version}"
	container.jvmFlags = listOf("-Xms256m", "-Xmx256m")
	from.platforms.set(listOf(amd64, arm64))
}

tasks.register("dockerImageNative") { description= "Native Image"; group = "build"; dependsOn("bootBuildImage") }
tasks.named<BootBuildImage>("bootBuildImage") {
	val nativeImageName = "${dockerRegistry}/${project.name}-native" + (if (System.getProperty("os.arch").equals("aarch64")) "-arm64v8" else "") + ":${project.version}"
	builder.set("paketobuildpacks/builder-jammy-buildpackless-tiny")
	buildpacks.add(nativeBuilder)
	imageName.set(nativeImageName)
	environment.set(mapOf("BP_NATIVE_IMAGE" to "true", "BP_JVM_VERSION" to "21", "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-J-Xmx6000m -march=compatibility"))
	doLast {
		exec { commandLine("/bin/sh", "-c", "docker run --rm $nativeImageName -check-integrity") }
		exec { commandLine("/bin/sh", "-c", "docker push $nativeImageName") }
	}
}

configure<net.researchgate.release.ReleaseExtension> {
	buildTasks.set(listOf("build", "test", "jib", "dockerImageNative"))
	tagTemplate.set("v${version}".replace("-SNAPSHOT", ""))
}

tasks.cyclonedxBom {  setIncludeConfigs(listOf("compileClasspath")) }