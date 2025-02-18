package dev.jsinco.brewery.breweries;


import dev.jsinco.brewery.util.Registry;

import java.util.Locale;

public enum BarrelType {

    ANY,
    OAK,
    BIRCH,
    SPRUCE,
    JUNGLE,
    ACACIA,
    DARK_OAK,
    CRIMSON,
    WARPED,
    CHERRY,
    BAMBOO;

    public String key() {
        return Registry.brewerySpacedKey(name().toLowerCase(Locale.ROOT));
    }
}
