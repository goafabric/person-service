# Stage 1: Build with Gradle + JDK
FROM gradle:9.0.0-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon -x test

# Stage 2: Generate CDS archive
FROM ibm-semeru-runtimes:open-21.0.8_9-jre AS cds-gen
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
RUN java -XX:ArchiveClassesAtExit=application.jsa \
         -Dspring.context.exit=onRefresh \
         -jar app.jar

# Stage 3: Final runtime with CDS
FROM ibm-semeru-runtimes:open-21.0.8_9-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
COPY --from=cds-gen /app/application.jsa application.jsa

ENTRYPOINT ["java", "-XX:SharedArchiveFile=application.jsa", "-jar", "app.jar"]
