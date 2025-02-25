plugins {
    id("java")
    id("net.neoforged.gradle.userdev") version "7.0.181"
}

group = "dev.jsinco.brewery"
version = properties["mod_version"]!!

repositories {
    maven("https://maven.neoforged.net/releases")
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    compileOnly("net.neoforged:neoforge:${properties["neo_version"]}")
}

tasks.test {
    useJUnitPlatform()
}