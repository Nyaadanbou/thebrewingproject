package dev.jsinco.brewery.util;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;

import java.util.Map;

public class Registry {

    public static final Map<String, BarrelType> BARREL_TYPE = getBarrelTypes();
    public static final Map<String, CauldronType> CAULDRON_TYPE = getEnumValues();

    private static Map<String, CauldronType> getEnumValues() {
        ImmutableMap.Builder<String, CauldronType> outputBuilder = ImmutableMap.builder();
        for (CauldronType barrelType : CauldronType.values()) {
            outputBuilder.put(barrelType.key(), barrelType);
        }
        return outputBuilder.build();
    }

    private static Map<String, BarrelType> getBarrelTypes() {
        ImmutableMap.Builder<String, BarrelType> outputBuilder = ImmutableMap.builder();
        for (BarrelType barrelType : BarrelType.values()) {
            outputBuilder.put(barrelType.key(), barrelType);
        }
        return outputBuilder.build();
    }

    public static String brewerySpacedKey(String key) {
        return "brewery:" + key;
    }
}
