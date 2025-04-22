package dev.jsinco.brewery;

import dev.jsinco.brewery.brew.BrewManager;

public interface TheBrewingProjectApi {


    <I> BrewManager<I> getBrewManager();
}
