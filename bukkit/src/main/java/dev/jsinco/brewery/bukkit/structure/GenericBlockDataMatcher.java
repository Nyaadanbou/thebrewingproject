package dev.jsinco.brewery.bukkit.structure;

import dev.jsinco.brewery.api.structure.BlockMatcherReplacement;
import dev.jsinco.brewery.bukkit.api.BukkitAdapter;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.MultipleFacing;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GenericBlockDataMatcher implements BlockDataMatcher<Void> {

    private final List<BlockMatcherReplacement> ambiguities;

    public GenericBlockDataMatcher(List<BlockMatcherReplacement> ambiguities) {
        this.ambiguities = ambiguities;
    }

    @Override
    public boolean matches(BlockData actual, BlockData expected, Void ignored) {
        // Avoid being too strict, think of fences for example
        if (expected instanceof MultipleFacing) {
            return materialMatches(expected.getMaterial(), actual.getMaterial());
        }
        if (expected.getMaterial() == Material.DECORATED_POT) {
            return actual.getMaterial() == Material.DECORATED_POT;
        }
        return materials(expected.getMaterial()).map(material -> {
                    BlockData revised = material.createBlockData();
                    expected.copyTo(revised);
                    return revised;
                })
                .anyMatch(actual::matches);
    }

    @Override
    public Set<Material> findStructureMaterials(Void matcherType, BreweryStructure structure) {
        return structure.getPalette().stream()
                .map(BlockData::getMaterial)
                .flatMap(this::materials)
                .collect(Collectors.toSet());
    }

    private Stream<Material> materials(Material expected) {
        return Stream.concat(
                ambiguities.stream()
                        .filter(ambiguity -> expected.equals(BukkitAdapter.toMaterial(ambiguity.original())))
                        .flatMap(ambiguity -> ambiguity.alternatives().stream())
                        .map(BukkitAdapter::toMaterial),
                Stream.of(expected)
        );
    }

    public boolean materialMatches(Material expected, Material actual) {
        if (ambiguities.stream()
                .filter(ambiguities -> expected.equals(BukkitAdapter.toMaterial(ambiguities.original())))
                .anyMatch(ambiguities -> ambiguities.alternatives().contains(BukkitAdapter.toMaterialHolder(actual)) || ambiguities.original().equals(BukkitAdapter.toMaterialHolder(actual)))
        ) {
            return true;
        }
        return expected == actual;
    }
}
