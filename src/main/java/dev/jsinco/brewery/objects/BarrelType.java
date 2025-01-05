package dev.jsinco.brewery.objects;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

import java.util.regex.Pattern;

public record BarrelType(NamespacedKey key, Pattern pattern) {

    public boolean matches(Material material) {
        String key = material.getKey().getKey();
        return pattern.matcher(key).find();
    }

    @Override
    public String toString() {
        return "BarrelType(" + key + ")";
    }
}
