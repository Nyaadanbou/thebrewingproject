package dev.jsinco.brewery.structure;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public enum StructureType {

    BARREL(StructureMeta.INVENTORY_SIZE, StructureMeta.USE_BARREL_SUBSTITUTION), DISTILLERY(StructureMeta.INVENTORY_SIZE);

    private final Set<StructureMeta<?, ?>> mandatoryMeta;

    StructureType(StructureMeta<?, ?>... mandatoryMeta) {
        this.mandatoryMeta = Arrays.stream(mandatoryMeta).collect(Collectors.toSet());
    }

    public List<StructureMeta<?, ?>> getMissingMandatory(Collection<StructureMeta<?, ?>> actualMeta) {
        return mandatoryMeta.stream().filter(value -> !actualMeta.contains(value)).toList();
    }
}
