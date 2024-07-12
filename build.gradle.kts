import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.kover)
}

buildscript {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjvm-default=all")
        jvmTarget = JvmTarget.JVM_21
    }
}

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

application {
    mainClass = "ru.yarsu.WebApplicationKt"
}

dependencies {
    implementation(libs.kotlinStdlib)
    implementation(libs.bundles.http4k)
    implementation(libs.bundles.logging)
    implementation(libs.jwtJava)
    implementation(libs.bundles.jackson)
    implementation(libs.slf4j)
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.bundles.http4kTesting)
    testImplementation(libs.mockito)
}

tasks.withType<Test> {
    useJUnitPlatform()
}
