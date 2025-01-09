package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.util.Registry;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public enum CauldronType {

    WATER(Material.WATER_CAULDRON),
    LAVA(Material.LAVA_CAULDRON),
    SNOW(Material.POWDER_SNOW_CAULDRON);

    public static final PdcType PDC_TYPE = new PdcType();
    private Material material;


    CauldronType(Material material) {
        this.material = material;
    }

    public Material material() {
        return material;
    }

    public NamespacedKey key() {
        return Registry.brewerySpacedKey(name().toLowerCase(Locale.ROOT));
    }

    public static @Nullable CauldronType from(Material type) {
        for (CauldronType cauldronType : Registry.CAULDRON_TYPE.values()) {
            if (cauldronType.material() == type) {
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
