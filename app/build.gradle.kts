import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.serialization") version "1.5.31"
    application
}

group = "com.github.wdonahoe.rpg-inventory"
version = "0.0.1"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")
    implementation("org.apache.commons:commons-lang3:3.0")
    implementation("org.apache.commons:commons-csv:1.5")
    implementation("commons-io:commons-io:2.5")
    implementation("com.github.kotlin-inquirer:kotlin-inquirer:v0.0.2-alpha")
    implementation("com.github.ajalt.mordant:mordant:2.0.0-beta2")
    implementation("com.github.ajalt.clikt:clikt:3.3.0")
    implementation("com.jakewharton.picnic:picnic:0.5.0")
    implementation("org.jline:jline:3.21.0")

    testImplementation(kotlin("test"))
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "com.github.wdonahoe.rpginventory.MainKt"
    }
}

application {
    mainClass.set("com.github.wdonahoe.rpginventory.MainKt")
}