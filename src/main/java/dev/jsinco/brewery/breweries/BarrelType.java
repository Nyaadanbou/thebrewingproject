package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.util.Registry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Pattern;

public record BarrelType(NamespacedKey key, Pattern pattern) {

    public static final BarrelType ANY = Registry.BARREL_TYPE.get(Registry.brewerySpacedKey("any"));

    public static final PdcType PDC_TYPE = new PdcType();

    public boolean matches(Material material) {
        String key = material.getKey().getKey();
        return pattern.matcher(key).find();
    }

    @Override
    public String toString() {
        return "BarrelType(" + key + ")";
    }

    public static class PdcType implements PersistentDataType<String, BarrelType> {

        @NotNull
        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @NotNull
        @Override
        public Class<BarrelType> getComplexType() {
            return BarrelType.class;
        }

        @NotNull
        @Override
        public String toPrimitive(@NotNull BarrelType complex, @NotNull PersistentDataAdapterContext context) {
            return complex.key().toString();
        }

        @NotNull
        @Override
        public BarrelType fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
            return Registry.BARREL_TYPE.get(NamespacedKey.fromString(primitive));
        }
    }
}
