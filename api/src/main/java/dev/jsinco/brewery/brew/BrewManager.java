package dev.jsinco.brewery.brew;

import java.util.List;
import java.util.Optional;

public interface BrewManager<I> {

    Brew createBrew(List<BrewingStep> steps);

    Brew createBrew(BrewingStep.Cook cookStep);

    Brew createBrew(BrewingStep.Mix mixStep);

    I toItem(Brew brew);

    Optional<Brew> fromItem(I item);
}
