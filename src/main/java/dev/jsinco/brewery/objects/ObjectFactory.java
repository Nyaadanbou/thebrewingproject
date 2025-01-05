package dev.jsinco.brewery.objects;

import dev.jsinco.brewery.structure.BreweryStructure;
import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.util.Registry;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjectFactory {

    private static final Pattern LARGE_BARREL_RE = Pattern.compile("^large_barrel\\$(.*)");
    private static final Pattern SMALL_BARREL_RE = Pattern.compile("^small_barrel\\$(.*)");

    private ObjectFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static Destroyable newObjectFromStructure(PlacedBreweryStructure placedBreweryStructure) {
        BreweryStructure breweryStructure = placedBreweryStructure.getStructure();
        String name = breweryStructure.getName();
        Matcher largeBarrelMatcher = LARGE_BARREL_RE.matcher(name);
        if (largeBarrelMatcher.find()) {
            return new Barrel(placedBreweryStructure, 27, Registry.BARREL_TYPE.get(Registry.brewerySpacedKey(largeBarrelMatcher.group(1))));
        }
        Matcher smallBarrelMatcher = SMALL_BARREL_RE.matcher(name);
        if (smallBarrelMatcher.find()) {
            return new Barrel(placedBreweryStructure, 9, Registry.BARREL_TYPE.get(Registry.brewerySpacedKey(smallBarrelMatcher.group(1))));
        }
        throw new IllegalArgumentException("Structure does not link to any object type");
    }
}
