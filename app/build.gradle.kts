plugins {
    kotlin("jvm") version "2.0.0"
    kotlin("plugin.serialization") version "2.0.0"
    application
}

group = "com.github.wdonahoe.rpg-inventory"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.14.0")
    implementation("org.apache.commons:commons-csv:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.1")
    implementation("commons-io:commons-io:2.16.1")
    implementation("com.github.kotlin-inquirer:kotlin-inquirer:0.1.0")
    implementation("com.github.ajalt.mordant:mordant:2.7.1")
    implementation("com.github.ajalt.clikt:clikt:4.4.0")
    implementation("com.jakewharton.picnic:picnic:0.7.0")
    implementation("org.jline:jline:3.26.3")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
    implementation(kotlin("script-runtime"))
}

tasks.test {
    useJUnit()
}

kotlin {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
    }
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.github.wdonahoe.rpginventory.MainKt"
    }
}

application {
    mainClass.set("com.github.wdonahoe.rpginventory.MainKt")
}