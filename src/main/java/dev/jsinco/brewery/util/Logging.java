package dev.jsinco.brewery.util;

import dev.jsinco.brewery.TheBrewingProject;

public final class Logging {

    public static void log(String message) {
        TheBrewingProject.getInstance().getLogger().info(message);
    }

    public static void error(String message, Throwable throwable) {
        TheBrewingProject.getInstance().getLogger().severe(message);
    }

    public static void warning(String message) {
        TheBrewingProject.getInstance().getLogger().warning(message);
    }
}
