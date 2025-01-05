package dev.jsinco.brewery.util;

import com.google.common.collect.ImmutableMap;
import dev.jsinco.brewery.objects.BarrelType;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registry {

    public static final Map<NamespacedKey, BarrelType> BARREL_TYPE = compileBarrelTypes();

    private static Map<NamespacedKey, BarrelType> compileBarrelTypes() {
        ImmutableMap.Builder<NamespacedKey, BarrelType> outputBuilder = ImmutableMap.builder();
        Pattern nameFinderPattern = Pattern.compile("(.*)_planks$");
        for (Material plank_type : Tag.PLANKS.getValues()) {
            String key = plank_type.getKey().getKey();
            Matcher matcher = nameFinderPattern.matcher(key);
            if (!matcher.find()) {
                continue;
            }
            String name = matcher.group(1);
            NamespacedKey namespacedKey = brewerySpacedKey(name);
            outputBuilder.put(namespacedKey, new BarrelType(namespacedKey, Pattern.compile("^" + name)));
        }
        return outputBuilder.build();
    }

    public static NamespacedKey brewerySpacedKey(String key) {
        return NamespacedKey.fromString("brewery:" + key);
    }
}
