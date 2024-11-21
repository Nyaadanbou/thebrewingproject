package dev.jsinco.brewery.structure;

import com.google.common.base.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StructureRegistry {

    private final Map<String, BreweryStructure> structures = new HashMap<>();

    public Optional<BreweryStructure> getStructure(@NotNull String key) {
        Preconditions.checkNotNull(key);
        return Optional.ofNullable(structures.get(key));
    }

    public void addStructure(@NotNull String key, @NotNull BreweryStructure structure) {
        Preconditions.checkNotNull(structure);
        Preconditions.checkNotNull(key);
        structures.put(key, structure);
    }
}
