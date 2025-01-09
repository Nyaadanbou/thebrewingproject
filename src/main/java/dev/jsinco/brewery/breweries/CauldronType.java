package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.util.Registry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record CauldronType(NamespacedKey key, Material material) {

    public static final CauldronType WATER = Registry.CAULDRON_TYPE.get(Registry.brewerySpacedKey("water"));

    public static final PdcType PDC_TYPE = new PdcType();

    public static @Nullable CauldronType from(Material type) {
        for (CauldronType cauldronType : Registry.CAULDRON_TYPE.values()) {
            if ((cauldronType.key().getKey() + "_cauldron").equals(type.getKey().getKey())) {
                return cauldronType;
            }
        }
        return null;
    }

    public static class PdcType implements PersistentDataType<String, CauldronType> {

        @NotNull
        @Override
        public Class<String> getPrimitiveType() {
            return String.class;
        }

        @NotNull
        @Override
        public Class<CauldronType> getComplexType() {
            return CauldronType.class;
        }

        @NotNull
        @Override
        public String toPrimitive(@NotNull CauldronType complex, @NotNull PersistentDataAdapterContext context) {
            return complex.key().toString();
        }

        @NotNull
        @Override
        public CauldronType fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
            return Registry.CAULDRON_TYPE.get(NamespacedKey.fromString(primitive));
        }
    }
}
