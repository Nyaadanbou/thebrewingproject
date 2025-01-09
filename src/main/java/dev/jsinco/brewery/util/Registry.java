package dev.jsinco.brewery.util;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registry {

    public static final Map<NamespacedKey, BarrelType> BARREL_TYPE = compileBarrelTypes();
    public static final Map<NamespacedKey, CauldronType> CAULDRON_TYPE = compileCauldronTypes();

    private static Map<NamespacedKey, CauldronType> compileCauldronTypes() {
        ImmutableMap.Builder<NamespacedKey, CauldronType> outputBuilder = ImmutableMap.builder();
        Pattern cauldronPattern = Pattern.compile("(.*)_cauldron");
        for (Material cauldronType : Tag.CAULDRONS.getValues()) {
            String key = cauldronType.getKey().getKey();
            Matcher matcher = cauldronPattern.matcher(key);
            if (!matcher.find()) {
                continue;
            }
            NamespacedKey namespacedKey = brewerySpacedKey(matcher.group(1));
            outputBuilder.put(namespacedKey, new CauldronType(namespacedKey, cauldronType));
        }
        return outputBuilder.build();
    }

    private static Map<NamespacedKey, BarrelType> compileBarrelTypes() {
        ImmutableMap.Builder<NamespacedKey, BarrelType> outputBuilder = ImmutableMap.builder();
        Pattern nameFinderPattern = Pattern.compile("(.*)_planks$");
        for (Material plankType : Tag.PLANKS.getValues()) {
            String key = plankType.getKey().getKey();
            Matcher matcher = nameFinderPattern.matcher(key);
            if (!matcher.find()) {
                continue;
            }
            String name = matcher.group(1);
            NamespacedKey namespacedKey = brewerySpacedKey(name);
            outputBuilder.put(namespacedKey, new BarrelType(namespacedKey, Pattern.compile("^" + name)));
        }
        NamespacedKey any = brewerySpacedKey("any");
        outputBuilder.put(any, new BarrelType(any, Pattern.compile("")));
        return outputBuilder.build();
    }

    public static NamespacedKey brewerySpacedKey(String key) {
        return NamespacedKey.fromString("brewery:" + key);
    }
}
