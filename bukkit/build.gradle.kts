import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    id("java")
    id("io.github.goooler.shadow") version "8.1.7"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("de.eldoria.plugin-yml.bukkit") version "0.7.1"
}

group = "dev.jsinco.brewery"
version = rootProject.version

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.oraxen.com/releases")
    maven("https://maven.devs.beer/")
    maven("https://repo.nexomc.com/releases")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.codemc.org/repository/maven-public/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    compileOnly("org.projectlombok:lombok:1.18.30")
    compileOnly("org.jetbrains:annotations:24.0.0")
    compileOnly("io.th0rgal:oraxen:1.189.0")
    compileOnly("dev.lone:api-itemsadder:4.0.10")
    compileOnly("com.nexomc:nexo:1.1.0")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.13")
    compileOnly("com.github.GriefPrevention:GriefPrevention:17.0.0")

    implementation(project(":"))
    implementation("dev.thorinwasher.schem:schem-reader:1.0.0")
    implementation("com.github.Carleslc.Simple-YAML:Simple-Yaml:1.8.4")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.1")

    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.43.1")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("net.kyori:adventure-nbt:4.17.0")
    testImplementation("org.xerial:sqlite-jdbc:3.47.2.0")

    annotationProcessor("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
}

tasks {
    test {
        useJUnitPlatform()
    }


    runServer {
        minecraftVersion("1.21.4")
        /*
        downloadPlugins {
            modrinth("worldedit","DlD8WKr9")
        }
        */
    }

    shadowJar {
        archiveBaseName.set(rootProject.name)
        archiveClassifier.unset()
    }
}


bukkit {
    main = "dev.jsinco.brewery.bukkit.TheBrewingProject"
    foliaSupported = false
    apiVersion = "1.21"
    authors = listOf("Jsinco", "Mitality", "Thorinwasher")
    name = rootProject.name
    commands {
        register("brew") {
            aliases = listOf("brewery", "tbp")
        }
    }
    permissions {
        register("brewery.barrel.create") {
            children = listOf("brewery.barrel.access")
        }
        register("brewery.barrel.access")
        register("brewery.distillery.create") {
            children = listOf("brewery.distillery.access")
        }
        register("brewery.distillery.access")
        register("brewery.cauldron.access")
        register("brewery.structure.access") {
            children = listOf("brewery.barrel.access", "brewery.distillery.access", "brewery.cauldron.access")
        }
        register("brewery.structure.create") {
            children = listOf("brewery.structure.access", "brewery.barrel.create", "brewery.distillery.create")
            default = BukkitPluginDescription.Permission.Default.TRUE
        }
        register("brewery.command.create") {
        }
        register("brewery.command.status") {
        }
        register("brewery.command.event") {
        }
        register("brewery.command.reload") {
        }
        register("brewery.command.info") {
        }
        register("brewery.command.seal") {
        }
        register("brewery.command.other") {
        }
        register("brewery.command") {
            children = listOf(
                "brewery.command.create",
                "brewery.command.status",
                "brewery.command.event",
                "brewery.command.reload",
                "brewery.command.info",
                "brewery.command.seal",
                "brewery.command.other"
            )
            default = BukkitPluginDescription.Permission.Default.OP
        }
        register("brewery") {
            children = listOf("brewery.command", "brewery.structure.create")
        }
    }
    softDepend = listOf("oraxen", "itemsadder", "nexo", "WorldGuard")
}