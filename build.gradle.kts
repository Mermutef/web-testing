import org.gradle.api.JavaVersion.VERSION_21
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    application
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
    id("io.gitlab.arturbosch.detekt") version ("1.23.3")
}

detekt {
    allRules = true
    buildUponDefaultConfig = true
}

buildscript {
    repositories {
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
    }
}

val http4kVersion: String by project
val http4kConnectVersion: String by project
val junitVersion: String by project
val kotlinVersion: String by project
val ktlintVersion: String by project
val kotestVersion: String by project

ktlint {
    version.set(ktlintVersion)
}

application {
    mainClass = "ru.yarsu.WebApplicationKt"
}

tasks.withType<JavaExec> {
    standardInput = System.`in`
}

repositories {
    mavenCentral()
}

apply(plugin = "kotlin")

tasks {
    withType<KotlinCompile> {
        kotlinOptions {
            allWarningsAsErrors = false
            jvmTarget = "21"
            freeCompilerArgs += "-Xjvm-default=all"
        }
    }

    withType<Test> {
        useJUnitPlatform()
    }

    java {
        sourceCompatibility = VERSION_21
        targetCompatibility = VERSION_21
    }

    test {
        useJUnitPlatform()
    }
}

dependencies {
    implementation("com.auth0:java-jwt:4.4.0")
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("io.konform:konform-jvm:0.4.0")
    implementation("ch.qos.logback:logback-classic:1.5.4")
    implementation("org.http4k:http4k-client-okhttp:$http4kVersion")
    implementation("org.http4k:http4k-cloudnative:$http4kVersion")
    implementation("org.http4k:http4k-core:$http4kVersion")
    implementation("org.http4k:http4k-format-jackson:$http4kVersion")
    implementation("org.http4k:http4k-multipart:$http4kVersion")
    implementation("org.http4k:http4k-server-netty:$http4kVersion")
    implementation("org.http4k:http4k-template-pebble:$http4kVersion")
    implementation("org.http4k:http4k-cloudnative:$http4kVersion")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.2")
    testImplementation("org.http4k:http4k-testing-approval:$http4kVersion")
    testImplementation("org.http4k:http4k-testing-hamkrest:$http4kVersion")
    testImplementation("org.http4k:http4k-testing-kotest:$http4kVersion")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("io.kotest:kotest-runner-junit5:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}
