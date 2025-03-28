plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
}

group = "dev.jsinco.brewery"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.oraxen.com/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly("io.th0rgal:oraxen:1.163.0")

    implementation(project(":"))
    implementation("dev.thorinwasher.schem:schem-reader:1.0.0")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.1")

    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.43.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("net.kyori:adventure-nbt:4.17.0")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
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
    main = "dev.jsinco.brewery.bukkit.TheBrewingProject"
    foliaSupported = false
    apiVersion = "1.21"
    authors = listOf("Jsinco", "Mitality", "Thorinwasher")
    name = rootProject.name
    commands {
        register("test") {

        }
        register("brew") {
            aliases = listOf("brewery")
        }
    }
}