val ktorVersion = "3.0.1"
val opentelemetryAgentVersion = "2.10.0"
val logbackVersion = "1.5.12"

plugins {
    idea
    application
    kotlin("jvm") version "2.0.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    mavenCentral()
}

val agents: Configuration by configurations.creating
dependencies {
    //ktor
    implementation(platform("io.ktor:ktor-bom:$ktorVersion"))
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-jetty-jakarta")
    implementation("io.ktor:ktor-client-okhttp")

    // opentelemetry
    implementation("io.opentelemetry.instrumentation:opentelemetry-ktor-3.0:$opentelemetryAgentVersion-alpha")
    implementation("io.opentelemetry.javaagent:opentelemetry-javaagent:$opentelemetryAgentVersion")
    implementation("io.ktor:ktor-client-okhttp-jvm:3.0.1")
    agents("io.opentelemetry.javaagent:opentelemetry-javaagent:$opentelemetryAgentVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass = "org.example.AppKt"
}

val copyAgentTaskName = "copyAgent"
val copyAgentDestinationDirectory = File("build/agents")
tasks {
    create(copyAgentTaskName, type = Copy::class) {
        from(agents) {
            destinationDir = copyAgentDestinationDirectory
            rename("(opentelemetry-javaagent)(-.*)(\\.jar)", "$1$3")
        }
    }

    shadowJar {
        mergeServiceFiles()
        archiveBaseName.set("app")
        archiveClassifier.set("")
        archiveVersion.set("")
        dependencies {
            exclude(dependency("io.opentelemetry.javaagent:opentelemetry-javaagent:$opentelemetryAgentVersion"))
        }
    }

}

tasks.assemble {
    dependsOn(copyAgentTaskName)
}


