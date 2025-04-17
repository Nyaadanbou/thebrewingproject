package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.brew.BrewingStep;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.ingredient.SimpleIngredient;
import dev.jsinco.brewery.bukkit.util.BukkitAdapter;
import dev.jsinco.brewery.database.PersistenceException;
import dev.jsinco.brewery.database.sql.Database;
import dev.jsinco.brewery.util.moment.Moment;
import dev.jsinco.brewery.util.moment.PassedMoment;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.MockBukkitInject;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockBukkitExtension.class)
class BukkitMixerDataTypeTest {

    private Database database;
    @MockBukkitInject
    ServerMock serverMock;
    private @NotNull WorldMock world;

    @BeforeEach
    void setUp() {
        this.database = MockBukkit.load(TheBrewingProject.class).getDatabase();
        this.world = serverMock.addSimpleWorld("world");
    }

    @Test
    void checkPersistence() throws PersistenceException {
        Brew brew = new Brew(
                new BrewingStep.Mix(new PassedMoment(60 * Moment.MINUTE), Map.of(new SimpleIngredient(Material.WHEAT), 3))
        );
        Block block = world.getBlockAt(10, 10, 10);
        block.setType(Material.CAULDRON);
        BreweryLocation location = BukkitAdapter.toBreweryLocation(block);
        BukkitMixer mixer = new BukkitMixer(location, brew);
        database.insertValue(BukkitMixerDataType.INSTANCE, mixer);
        BukkitMixer mixer1 = database.find(BukkitMixerDataType.INSTANCE, world.getUID()).getFirst();
        assertEquals(brew, mixer1.getBrew());
        assertEquals(location, mixer1.position());
        Brew modifiedBrew = brew.withStep(new BrewingStep.Distill(4));
        BukkitMixer updatedMixer = new BukkitMixer(location, modifiedBrew);
        database.updateValue(BukkitMixerDataType.INSTANCE, updatedMixer);
        List<BukkitMixer> mixers = database.find(BukkitMixerDataType.INSTANCE, world.getUID());
        assertEquals(1, mixers.size());
        BukkitMixer mixer2 = mixers.getFirst();
        assertEquals(location, mixer2.position());
        assertEquals(modifiedBrew, mixer2.getBrew());
    }
}