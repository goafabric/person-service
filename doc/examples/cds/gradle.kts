// docker run --pull always --name person-service -e 'JAVA_TOOL_OPTIONS=-Xmx256m' --rm -p50800:50800 goafabric/person-service-cds:3.5.1-SNAPSHOT
// docker run --pull always --name person-service -e 'JAVA_TOOL_OPTIONS=-Xmx256m' --rm -p50800:50800 goafabric/person-service-cds:$(grep '^version=' gradle.properties | cut -d'=' -f2)
// -Dspring.context.exit=onRefresh
// https://github.com/paketo-buildpacks/native-image/issues/374

interface InjectedExecOps { @get:Inject val execOps: ExecOperations }
tasks.register("dockerJvmImage") { description= "JVM Image"; group = "build"; dependsOn("bootBuildImage") }
tasks.named<BootBuildImage>("bootBuildImage") {
    val imageName = "${dockerRegistry}/${project.name}-cds:${project.version}"
    this.imageName.set(imageName)
    environment.set(mapOf("BP_NATIVE_IMAGE" to "false", "BP_JVM_VERSION" to "21", "BP_JVM_CDS_ENABLED" to "true"))
    doLast {
        project.objects.newInstance<InjectedExecOps>().execOps.exec { commandLine("/bin/sh", "-c", "docker run --rm $imageName -check-integrity") }
        project.objects.newInstance<InjectedExecOps>().execOps.exec { commandLine("/bin/sh", "-c", "docker push $imageName") }
    }
}
