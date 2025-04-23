package dev.jsinco.brewery.bukkit.structure;

import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.Nullable;

public interface BlockDataMatcher<T> {

    boolean matches(BlockData actual, BlockData expected, T matcherType);

    @Nullable
    Material findSubstitution(BlockData expected, T matcherType);
}
