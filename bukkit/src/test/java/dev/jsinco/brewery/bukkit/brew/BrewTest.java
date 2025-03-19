package dev.jsinco.brewery.bukkit.brew;

import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.ingredient.SimpleIngredient;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkitExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(MockBukkitExtension.class)
public class BrewTest {

    @Test
    void equals() {
        Brew<ItemStack> brew1 = new Brew<>(
                new PassedMoment(20),
                Map.of(SimpleIngredient.of("wheat").get(), 1),
                new PassedMoment(20),
                2,
                CauldronType.LAVA,
                BarrelType.ACACIA
        );
        Brew<ItemStack> brew2 = new Brew<>(
                new PassedMoment(20),
                Map.of(SimpleIngredient.of("wheat").get(), 1),
                new PassedMoment(20),
                2,
                CauldronType.LAVA,
                BarrelType.ACACIA
        );
        assertEquals(brew1, brew2);
    }

    @Test
    void equals_notEqual() {
        Brew<ItemStack> brew1 = new Brew<>(
                new PassedMoment(20),
                Map.of(SimpleIngredient.of("wheat").get(), 1),
                new PassedMoment(20),
                3,
                CauldronType.LAVA,
                BarrelType.ACACIA
        );
        Brew<ItemStack> brew2 = new Brew<>(
                new PassedMoment(20),
                Map.of(SimpleIngredient.of("wheat").get(), 1),
                new PassedMoment(20),
                2,
                CauldronType.LAVA,
                BarrelType.ACACIA
        );
        assertNotEquals(brew1, brew2);
    }

    @Test
    void equals_notEquals2() {
        Brew<ItemStack> brew1 = new Brew<>(
                new PassedMoment(20),
                Map.of(SimpleIngredient.of("wheat").get(), 2),
                new PassedMoment(20),
                2,
                CauldronType.LAVA,
                BarrelType.ACACIA
        );
        Brew<ItemStack> brew2 = new Brew<>(
                new PassedMoment(20),
                Map.of(SimpleIngredient.of("wheat").get(), 1),
                new PassedMoment(20),
                2,
                CauldronType.LAVA,
                BarrelType.ACACIA
        );
        assertNotEquals(brew1, brew2);
    }
}
