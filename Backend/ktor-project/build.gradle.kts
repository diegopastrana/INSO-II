plugins {
    // Kotlin JVM
    id("org.jetbrains.kotlin.jvm") version "1.8.10"
    // Plugin oficial de Ktor para Gradle (versión 3.1.2 que ya usas en dependencias)
    id("io.ktor.plugin") version "3.1.2"
    // Plugin de serialización de Kotlin
    kotlin("plugin.serialization") version "1.8.10"
    // Plugin application para poder definir mainClass
    application
    // ShadowJar (misma versión 8.3.6 que requiere el plugin de Ktor internamente)
    id("com.github.johnrengelman.shadow") version "8.3.6"
}

group = "com.example"
version = "0.0.1"

application {
    // Main class de Ktor con Netty
    mainClass.set("io.ktor.server.netty.EngineMain")

    // Mantenemos la lógica para modo “development” si queremos pasar el flag -Pdevelopment
    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("ch.qos.logback:logback-core:1.5.18")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    implementation("org.jetbrains.exposed:exposed-core:0.61.0")
    implementation("org.jetbrains.exposed:exposed-dao:0.61.0")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.61.0")
    implementation("org.jetbrains.exposed:exposed-java-time:0.61.0")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.1")

    implementation("io.ktor:ktor-client-cio:3.1.2")
    implementation("io.ktor:ktor-server-auth:3.1.2")
    implementation("io.ktor:ktor-server-auth-jwt:3.1.2")
    implementation("io.ktor:ktor-server-netty:3.1.2")
    implementation("io.ktor:ktor-server-config-yaml-jvm:3.1.2")
    implementation("io.ktor:ktor-server-content-negotiation:3.1.2")
    implementation("io.ktor:ktor-client-content-negotiation:3.1.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.2")
    implementation("io.ktor:ktor-server-cors:3.1.2")

    implementation("org.postgresql:postgresql:42.7.2")
    implementation("com.stripe:stripe-java:29.1.0")
    implementation("com.auth0:java-jwt:4.5.0")
    implementation("commons-codec:commons-codec:1.18.0")

    testImplementation("io.ktor:ktor-server-test-host:3.1.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:1.8.10")
}

// Configuración de ShadowJar para generar un fat-jar llamado "app.jar"
tasks {
    shadowJar {
        archiveFileName.set("app.jar")
        mergeServiceFiles()
    }
}
