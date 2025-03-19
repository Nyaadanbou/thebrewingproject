package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.structure.MultiBlockStructure;

public interface StructureHolder<H extends StructureHolder<H>> {

    MultiBlockStructure<H> getStructure();

    void destroy();
}
