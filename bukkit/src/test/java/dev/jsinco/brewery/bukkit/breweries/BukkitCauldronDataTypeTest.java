package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.Cauldron;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.ingredient.SimpleIngredient;
import dev.jsinco.brewery.database.Database;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.MockBukkitInject;
import org.mockbukkit.mockbukkit.ServerMock;
import org.mockbukkit.mockbukkit.world.WorldMock;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockBukkitExtension.class)
class BukkitCauldronDataTypeTest {
    @MockBukkitInject
    ServerMock server;
    private @NotNull WorldMock world;
    private Database database;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        this.world = server.addSimpleWorld("hello!");
        TheBrewingProject theBrewingProject = MockBukkit.load(TheBrewingProject.class);
        this.database = theBrewingProject.getDatabase();
    }

    @Test
    void checkPersistence() throws IOException, SQLException {
        BukkitCauldron cauldron = new BukkitCauldron(Map.of(new SimpleIngredient(Material.OAK_PLANKS), 10), world.getBlockAt(0, 0, 0), 103);
        database.insertValue(BukkitCauldronDataType.INSTANCE, cauldron);
        List<Cauldron<ItemStack>> cauldrons = database.retrieveAll(BukkitCauldronDataType.INSTANCE, world.getUID());
        assertEquals(1, cauldrons.size());
        Cauldron<ItemStack> retrievedCauldron = cauldrons.get(0);
        assertEquals(cauldron.ingredients(), retrievedCauldron.ingredients());
        assertEquals(cauldron.position(), retrievedCauldron.position());
        assertEquals(cauldron.brewStart(), retrievedCauldron.brewStart());
        BukkitCauldron updatedValue = new BukkitCauldron(Map.of(new SimpleIngredient(Material.OAK_PLANKS), 11), world.getBlockAt(0, 0, 0), 104);
        database.updateValue(BukkitCauldronDataType.INSTANCE, updatedValue);
        List<Cauldron<ItemStack>> updatedCauldrons = database.retrieveAll(BukkitCauldronDataType.INSTANCE, world.getUID());
        assertEquals(1, updatedCauldrons.size());
        assertEquals(104, updatedCauldrons.get(0).brewStart());
        assertEquals(11, updatedCauldrons.get(0).ingredients().get(new SimpleIngredient(Material.OAK_PLANKS)));
        database.remove(BukkitCauldronDataType.INSTANCE, cauldron);
        assertEquals(0, database.retrieveAll(BukkitCauldronDataType.INSTANCE, world.getUID()).size());
    }
}