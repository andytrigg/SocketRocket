plugins {
    application
    kotlin("jvm") version "1.9.22"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21)) // Set to 21 (or 17 if needed)
    }
}

application {
    mainClass.set("com.sloshydog.socketrocket.MainKt")
}
tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

group = "com.sloshydog"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("io.mockk:mockk:1.13.9")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("at.favre.lib:bcrypt:0.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.6")
}

tasks.test {
    useJUnitPlatform()
}


