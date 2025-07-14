package dev.jsinco.brewery.bukkit.breweries;

import java.util.regex.Pattern;

public class BreweryFactory {

    private static final Pattern LARGE_BARREL_RE = Pattern.compile("^large_barrel\\$(.*)");
    private static final Pattern SMALL_BARREL_RE = Pattern.compile("^small_barrel\\$(.*)");

    private BreweryFactory() {
        throw new IllegalStateException("Utility class");
    }

}
