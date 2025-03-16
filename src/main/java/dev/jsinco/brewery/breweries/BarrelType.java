package dev.jsinco.brewery.breweries;


import dev.jsinco.brewery.util.Registry;

import java.util.Arrays;
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
    BAMBOO,
    COPPER;

    public static final BarrelType[] PLACEABLE_TYPES = Arrays.stream(values()).filter(barrelType -> barrelType != ANY).toArray(BarrelType[]::new);

    public String key() {
        return Registry.brewerySpacedKey(name().toLowerCase(Locale.ROOT));
    }
}
