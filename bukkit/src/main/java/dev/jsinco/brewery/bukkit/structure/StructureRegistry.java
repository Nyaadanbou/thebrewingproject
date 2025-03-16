package dev.jsinco.brewery.bukkit.structure;

import com.google.common.base.Preconditions;
import dev.jsinco.brewery.structure.StructureMeta;
import dev.jsinco.brewery.structure.StructureType;
import org.bukkit.Material;
import org.bukkit.block.data.BlockData;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class StructureRegistry {

    private final Map<String, BreweryStructure> structureNames = new HashMap<>();
    private final Map<StructureType, Map<Material, Set<BreweryStructure>>> structures = new EnumMap<>(StructureType.class);

    public Optional<BreweryStructure> getStructure(@NotNull String key) {
        Preconditions.checkNotNull(key);
        return Optional.ofNullable(structureNames.get(key));
    }

    public Set<BreweryStructure> getPossibleStructures(@NotNull Material material, StructureType structureType) {
        Preconditions.checkNotNull(material);
        return structures.computeIfAbsent(structureType, ignored -> new HashMap<>()).getOrDefault(material, Set.of());
    }

    public <T> void addStructure(@NotNull BreweryStructure structure, BlockDataMatcher<T> blockDataMatcher, T[] matcherTypes) {
        Preconditions.checkNotNull(structure);
        structureNames.put(structure.getName(), structure);
        for (BlockData blockData : structure.getPalette()) {
            for (T matcherType : matcherTypes) {
                structures.computeIfAbsent(structure.getMeta(StructureMeta.TYPE), ignored -> new HashMap<>())
                        .computeIfAbsent(blockDataMatcher.findSubstitution(blockData, matcherType), ignored -> new HashSet<>()).add(structure);
            }
        }
    }

    public void addStructure(@NotNull BreweryStructure structure) {
        Preconditions.checkNotNull(structure);
        structureNames.put(structure.getName(), structure);
        for (BlockData blockData : structure.getPalette()) {
            structures.computeIfAbsent(structure.getMeta(StructureMeta.TYPE), ignored -> new HashMap<>())
                    .computeIfAbsent(blockData.getMaterial(), ignored -> new HashSet<>()).add(structure);
        }
    }
}
