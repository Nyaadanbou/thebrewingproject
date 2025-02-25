rootProject.name = "TheBrewingProject"
include("bukkit")
include("neoforge")
pluginManagement {
    repositories {
        mavenCentral()
        maven("https://maven.neoforged.net/releases")
        gradlePluginPortal()
    }
}