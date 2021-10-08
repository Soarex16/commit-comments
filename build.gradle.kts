import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    kotlin("plugin.serialization") version "1.5.31"
    id("application")
}

group = "me.soarex"
version = "1.0"

application {
    mainClass.set("MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))

    implementation("com.github.ajalt.clikt", "clikt", "3.2.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.2.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}