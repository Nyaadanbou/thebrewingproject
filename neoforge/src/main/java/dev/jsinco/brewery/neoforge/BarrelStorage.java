package dev.jsinco.brewery.neoforge;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

public class BarrelStorage extends SavedData {

    public static BarrelStorage create() {
        return new BarrelStorage();
    }

    // Load existing instance of saved data
    public static BarrelStorage load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        BarrelStorage data = BarrelStorage.create();
        // Load saved data
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        return null;
    }
}
