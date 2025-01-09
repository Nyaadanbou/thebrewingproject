package dev.jsinco.brewery.util;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import org.bukkit.NamespacedKey;

import java.util.Map;

public class Registry<T> {

    public static final Map<NamespacedKey, BarrelType> BARREL_TYPE = getBarrelTypes();
    public static final Map<NamespacedKey, CauldronType> CAULDRON_TYPE = getEnumValues();

    private static Map<NamespacedKey, CauldronType> getEnumValues() {
        ImmutableMap.Builder<NamespacedKey, CauldronType> outputBuilder = ImmutableMap.builder();
        for (CauldronType barrelType : CauldronType.values()) {
            outputBuilder.put(barrelType.key(), barrelType);
        }
        return outputBuilder.build();
    }

    private static Map<NamespacedKey, BarrelType> getBarrelTypes() {
        ImmutableMap.Builder<NamespacedKey, BarrelType> outputBuilder = ImmutableMap.builder();
        for (BarrelType barrelType : BarrelType.values()) {
            outputBuilder.put(barrelType.key(), barrelType);
        }
        return outputBuilder.build();
    }

    public static NamespacedKey brewerySpacedKey(String key) {
        return NamespacedKey.fromString("brewery:" + key);
    }
}
