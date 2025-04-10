package dev.jsinco.brewery.bukkit.brew;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewingStep;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.bukkit.ingredient.SimpleIngredient;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkitExtension;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockBukkitExtension.class)
public class BrewTest {

    @Test
    void equals() {
        Brew brew1 = new Brew(
                List.of(
                        new BrewingStep.Cook(
                                new PassedMoment(20),
                                Map.of(SimpleIngredient.of("wheat").get(), 1),
                                CauldronType.LAVA
                        ),
                        new BrewingStep.Distill(
                                3
                        ),
                        new BrewingStep.Age(
                                new PassedMoment(20),
                                BarrelType.ACACIA
                        )
                )
        );
        Brew brew2 = new Brew(
                List.of(
                        new BrewingStep.Cook(
                                new PassedMoment(20),
                                Map.of(SimpleIngredient.of("wheat").get(), 1),
                                CauldronType.LAVA
                        ),
                        new BrewingStep.Distill(
                                3
                        ),
                        new BrewingStep.Age(
                                new PassedMoment(20),
                                BarrelType.ACACIA
                        )
                )
        );
        assertEquals(brew1, brew2);
    }

    @Test
    void equals_notEqual() {
        Brew brew1 = new Brew(
                List.of(
                        new BrewingStep.Cook(
                                new PassedMoment(20),
                                Map.of(SimpleIngredient.of("wheat").get(), 1),
                                CauldronType.LAVA
                        ),
                        new BrewingStep.Distill(
                                3
                        ),
                        new BrewingStep.Age(
                                new PassedMoment(20),
                                BarrelType.ACACIA
                        )
                )
        );
        Brew brew2 = new Brew(
                List.of(
                        new BrewingStep.Cook(
                                new PassedMoment(20),
                                Map.of(SimpleIngredient.of("wheat").get(), 1),
                                CauldronType.LAVA
                        ),
                        new BrewingStep.Distill(
                                2
                        ),
                        new BrewingStep.Age(
                                new PassedMoment(20),
                                BarrelType.ACACIA
                        )
                )
        );
        assertNotEquals(brew1, brew2);
    }

    @Test
    void equals_notEquals2() {
        Brew brew1 = new Brew(
                List.of(
                        new BrewingStep.Cook(
                                new PassedMoment(20),
                                Map.of(SimpleIngredient.of("wheat").get(), 1),
                                CauldronType.LAVA
                        ),
                        new BrewingStep.Distill(
                                2
                        ),
                        new BrewingStep.Age(
                                new PassedMoment(20),
                                BarrelType.ACACIA
                        )
                )
        );
        Brew brew2 = new Brew(
                List.of(
                        new BrewingStep.Cook(
                                new PassedMoment(20),
                                Map.of(SimpleIngredient.of("wheat").get(), 2),
                                CauldronType.LAVA
                        ),
                        new BrewingStep.Distill(
                                2
                        ),
                        new BrewingStep.Age(
                                new PassedMoment(20),
                                BarrelType.ACACIA
                        )
                )
        );
        assertNotEquals(brew1, brew2);
    }
}
