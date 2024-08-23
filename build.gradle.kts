import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
}

group = "com.toms223"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    implementation("org.ktorm:ktorm-core:4.1.0")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
    implementation("com.h2database:h2:2.1.214")
    implementation("org.http4k:http4k-core:5.27.0.0")
    implementation("org.http4k:http4k-server-jetty:5.27.0.0")
    implementation(files("libs/WinterBoot-1.0-SNAPSHOT.jar"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

tasks.withType<KotlinCompile>{
    kotlinOptions {
        javaParameters = true
    }
}