val version: String by project
val javaVersion = "21"
java.sourceCompatibility = JavaVersion.toVersion(javaVersion)

val artifactoryProxyUrl: String by project; val artifactoryDockerRegistry: String by project; val nativeImageVersionType: String? by project
val jvmImageVersionType: String? by project; val artifactoryUsername: String by project; val trustsourceApiKey: String? by project

val baseImage = """
FROM ibm-semeru-runtimes:open-21.0.8_9-jre@sha256:551139c6639d176c9591c2e2eee16b0092b97a31761c8a9202cf9fffc844d845
""".replaceFirst("FROM ", "").trim()

