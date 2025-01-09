package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.util.Registry;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

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

    public static final PdcType PDC_TYPE = new PdcType();

    public NamespacedKey key() {
        return Registry.brewerySpacedKey(name().toLowerCase(Locale.ROOT));
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
