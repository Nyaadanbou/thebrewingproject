package dev.jsinco.brewery.structure;

import dev.jsinco.brewery.breweries.Barrel;
import dev.jsinco.brewery.breweries.Distillery;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public record StructureType(String key, Class<?> tClass, StructureMeta<?>... mandatoryMeta) {

    public static final StructureType BARREL = new StructureType("barrel", Barrel.class, StructureMeta.INVENTORY_SIZE, StructureMeta.USE_BARREL_SUBSTITUTION);
    public static final StructureType DISTILLERY = new StructureType("distillery", Distillery.class, StructureMeta.INVENTORY_SIZE, StructureMeta.TAGGED_MATERIAL, StructureMeta.PROCESS_TIME);

    public List<StructureMeta<?>> getMissingMandatory(Collection<StructureMeta<?>> actualMeta) {
        return Arrays.stream(mandatoryMeta).filter(value -> !actualMeta.contains(value)).toList();
    }
}
