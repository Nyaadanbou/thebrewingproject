package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.structure.MultiBlockStructure;
import dev.jsinco.brewery.util.vector.BreweryLocation;

public interface StructureHolder<H extends StructureHolder<H>> {

    MultiBlockStructure<H> getStructure();

    void destroy(BreweryLocation breweryLocation);
}
