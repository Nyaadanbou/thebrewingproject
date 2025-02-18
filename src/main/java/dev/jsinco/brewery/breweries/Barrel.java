package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.structure.MultiBlockStructure;

import java.util.Optional;

public interface Barrel {

    void destroy();

    MultiBlockStructure getStructure();
}
