package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.structure.MultiBlockStructure;

public interface Barrel<B extends Barrel<B>> extends StructureHolder<B>, InventoryAccessible {

    void destroy();

    void tick();
}
