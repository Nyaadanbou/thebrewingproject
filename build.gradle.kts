plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
}

group = "dev.jsinco.brewery"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    compileOnly("org.projectlombok:lombok:1.18.30")

    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")
    implementation("com.zaxxer:HikariCP:6.2.1")
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly("com.google.guava:guava:33.4.0-jre")
    compileOnly("com.google.code.gson:gson:2.12.1")
    compileOnly("org.joml:joml:1.10.8")
    testImplementation("org.joml:joml:1.10.8")
    implementation("org.xerial:sqlite-jdbc:3.47.2.0")


    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
}

tasks {
    test {
        useJUnitPlatform()
    }


    runServer {
        minecraftVersion("1.21.4")
    }
}

bukkit {
    main = "dev.jsinco.brewery.TheBrewingProject"
    foliaSupported = false
    apiVersion = "1.21"
    authors = listOf("Jsinco", "Mitality", "Thorinwasher")
    commands {
        register("test") {

        }
    }
}