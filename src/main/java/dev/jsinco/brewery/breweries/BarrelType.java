package dev.jsinco.brewery.breweries;


import dev.jsinco.brewery.configuration.locale.TranslationsConfig;
import dev.jsinco.brewery.util.BreweryKey;

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

    public BreweryKey key() {
        return BreweryKey.parse(name().toLowerCase(Locale.ROOT));
    }

    public String translation() {
        return TranslationsConfig.BARREL_TYPE.get(this.name().toLowerCase(Locale.ROOT));
    }
}
