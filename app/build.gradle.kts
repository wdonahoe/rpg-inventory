import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "com.github.wdonahoe.rpg-inventory"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.apache.commons:commons-lang3:3.0")
    implementation("org.apache.commons:commons-csv:1.5")
    implementation("commons-io:commons-io:2.5")
    implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.3")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnit()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("com.github.wdonahoe.rpginventory.MainKt")
}