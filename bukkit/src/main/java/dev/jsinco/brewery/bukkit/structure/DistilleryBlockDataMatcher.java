package dev.jsinco.brewery.bukkit.structure;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public class DistilleryBlockDataMatcher implements BlockDataMatcher<Void> {

    public static final DistilleryBlockDataMatcher INSTANCE = new DistilleryBlockDataMatcher();

    @Override
    public boolean matches(BlockData actual, BlockData expected, Void ignored) {
        return actual.getMaterial() == expected.getMaterial();
    }

    @Override
    public @Nullable Material findSubstitution(BlockData expected, Void ignored) {
        return expected.getMaterial();
    }
}
