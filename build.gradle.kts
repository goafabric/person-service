import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

val version: String by project
val javaVersion = "25"
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)

val dockerRegistry = "goafabric"
val baseImage = "ibm-semeru-runtimes:open-jdk-25.0.0_36-jre@sha256:8ae073345116cfd51ec37b26c3a1c25de9336d436354e0be4271bda1463e119c"

plugins {
	java
	jacoco
	id("org.springframework.boot") version "4.0.0-M3"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.graalvm.buildtools.native") version "0.11.1"

	id("com.google.cloud.tools.jib") version "3.4.5"
	id("net.researchgate.release") version "3.1.0"
	id("org.sonarqube") version "6.3.1.5724"

	id("org.cyclonedx.bom") version "3.0.0"
	id("org.springdoc.openapi-gradle-plugin") version "1.9.0"
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	constraints {
		annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
		implementation("org.mapstruct:mapstruct:1.6.3")
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.12")
		implementation("io.github.resilience4j:resilience4j-spring-boot3:2.3.0")
		implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.1.2")

		testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")
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
	//implementation("net.ttddyy.observation:datasource-micrometer-spring-boot") //TODO

	//openapi
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui")

	//adapter
	implementation("io.github.resilience4j:resilience4j-spring-boot3")
	implementation("org.springframework.boot:spring-boot-starter-aspectj")

	//persistence
	implementation("org.springframework.boot:spring-boot-starter-data-jpa") {exclude("org.glassfish.jaxb", "jaxb-runtime")}
	implementation("com.h2database:h2")
	implementation("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")
	implementation("org.flywaydb:flyway-database-postgresql")

	//mongodb + elastic
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	//implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")

	//code generation
	implementation("org.mapstruct:mapstruct")
	annotationProcessor("org.mapstruct:mapstruct-processor")

	//test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	//devtools
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("com.tngtech.archunit:archunit-junit5")

	//spring boot 4.0
	implementation("org.springframework.boot:spring-boot-starter-opentelemetry")
	implementation("org.springframework.boot:spring-boot-starter-flyway")
	implementation("org.springframework.boot:spring-boot-starter-restclient")

}

tasks.withType<Test> {
	useJUnitPlatform()
	exclude("**/*NRIT*")
	finalizedBy("jacocoTestReport")
}
tasks.jacocoTestReport { reports {csv.required.set(true); xml.required.set(true) } }

jib {
	val amd64 = com.google.cloud.tools.jib.gradle.PlatformParameters(); amd64.os = "linux"; amd64.architecture = "amd64"; val arm64 = com.google.cloud.tools.jib.gradle.PlatformParameters(); arm64.os = "linux"; arm64.architecture = "arm64"
	from.image = baseImage
	to.image = "${dockerRegistry}/${project.name}:${project.version}"
	container.jvmFlags = listOf("-Xms256m", "-Xmx256m")
	from.platforms.set(listOf(amd64, arm64))
}

interface InjectedExecOps { @get:Inject val execOps: ExecOperations }
tasks.register("dockerImageNative") { description= "Native Image"; group = "build"; dependsOn("bootBuildImage") }
tasks.named<BootBuildImage>("bootBuildImage") {
	val nativeImageName = "${dockerRegistry}/${project.name}-native:${project.version}"
	imageName.set(nativeImageName)
	environment.set(mapOf("BP_NATIVE_IMAGE" to "true", "BP_JVM_VERSION" to javaVersion, "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-J-Xmx7000m -march=compatibility"))
	doLast {
		project.objects.newInstance<InjectedExecOps>().execOps.exec { commandLine("/bin/sh", "-c", "docker run --rm $nativeImageName -check-integrity") }
		project.objects.newInstance<InjectedExecOps>().execOps.exec { commandLine("/bin/sh", "-c", "docker push $nativeImageName") }
	}
}

configure<net.researchgate.release.ReleaseExtension> {
	buildTasks.set(listOf("build", "test", "jib", "dockerImageNative"))
	tagTemplate.set("v${version}".replace("-SNAPSHOT", ""))
}

openApi {
	outputDir.set(file("doc/generated"))
	customBootRun { args.set(listOf("--server.port=8080")) }
	tasks.forkedSpringBootRun { dependsOn("compileAotJava", "processAotResources") }
}
