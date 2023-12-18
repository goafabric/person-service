import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

group = "org.goafabric"
version = "3.2.0-kotlin-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

val dockerRegistry = "goafabric"
val nativeBuilder = "dashaun/builder:20231204"
val baseImage = "eclipse-temurin:21.0.1_12-jre@sha256:5f23c8fa909c5189c5f267447be5d8c3ffb031f4644958d868bfbf180fab44e5" //"ibm-semeru-runtimes:open-20.0.1_9-jre-focal@sha256:f1a10da50d02f51e79e3c9604ed078a39c19cd2711789cab7aa5d11071482a7e"
jacoco.toolVersion = "0.8.10"

plugins {
	jacoco
	id("org.springframework.boot") version "3.2.0"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.graalvm.buildtools.native") version "0.9.28"
	id("com.google.cloud.tools.jib") version "3.4.0"

	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
	kotlin("plugin.jpa") version "1.9.21"
	kotlin("kapt") version "1.9.21"
}

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	constraints {
		implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0")
		implementation("org.mapstruct:mapstruct:1.5.5.Final")
		annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")
		implementation("io.github.resilience4j:resilience4j-spring-boot3:2.1.0")
		implementation("net.ttddyy.observation:datasource-micrometer-spring-boot:1.0.2")
	}
}

dependencies {
	//web
	implementation("org.springframework.boot:spring-boot-starter-web")

	//monitoring
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.micrometer:micrometer-registry-prometheus")

	implementation("io.micrometer:micrometer-tracing-bridge-otel")
	implementation("io.opentelemetry:opentelemetry-exporter-otlp")

	implementation("net.ttddyy.observation:datasource-micrometer-spring-boot")

	//crosscuting
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	//adapter
	implementation("io.github.resilience4j:resilience4j-spring-boot3") {exclude ("io.github.resilience4j", "resilience4j-micrometer")} // has to be excluded because of aot processor problem with kotlin

	//code generation
	implementation("org.mapstruct:mapstruct")
	kapt("org.mapstruct:mapstruct-processor:1.5.5.Final")

	//persistence
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa") {exclude("org.glassfish.jaxb", "jaxb-runtime")}
	implementation("com.h2database:h2")
	implementation("org.postgresql:postgresql")
	implementation("org.flywaydb:flyway-core")

	//mongodb
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

	//test
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	//kotlin
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
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

tasks.register("dockerImageNative") { group = "build"; dependsOn("bootBuildImage") }
tasks.named<BootBuildImage>("bootBuildImage") {
	val nativeImageName = "${dockerRegistry}/${project.name}-native" + (if (System.getProperty("os.arch").equals("aarch64")) "-arm64v8" else "") + ":${project.version}"
	builder.set(nativeBuilder)
	imageName.set(nativeImageName)
	environment.set(mapOf("BP_NATIVE_IMAGE" to "true", "BP_JVM_VERSION" to "21", "BP_NATIVE_IMAGE_BUILD_ARGUMENTS" to "-J-Xmx5000m -march=compatibility"))
	doLast {
		exec { commandLine("/bin/sh", "-c", "docker run --rm $nativeImageName -check-integrity") }
		exec { commandLine("/bin/sh", "-c", "docker push $nativeImageName") }
	}
}

graalvmNative { //https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html#configuration-options
	binaries.named("main") { quickBuild.set(true) }
}