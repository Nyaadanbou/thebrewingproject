package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.structure.PlacedBreweryStructure;

import java.util.Optional;

public interface BehaviorHolder {

    void destroy();

    Optional<PlacedBreweryStructure> getStructure();
}
