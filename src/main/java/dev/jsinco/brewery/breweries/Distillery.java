package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.structure.MultiBlockStructure;

public interface Distillery<D extends Distillery<D>> extends StructureHolder<D>, InventoryAccessible {

    long getStartTime();

    void tick();
}
