package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.bukkit.structure.BreweryStructure;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.structure.StructureMeta;
import org.bukkit.Location;

import java.util.regex.Pattern;

public class BreweryFactory {

    private static final Pattern LARGE_BARREL_RE = Pattern.compile("^large_barrel\\$(.*)");
    private static final Pattern SMALL_BARREL_RE = Pattern.compile("^small_barrel\\$(.*)");

    private BreweryFactory() {
        throw new IllegalStateException("Utility class");
    }

}
