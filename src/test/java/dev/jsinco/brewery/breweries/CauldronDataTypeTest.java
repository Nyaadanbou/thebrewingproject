package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.recipes.ingredient.SimpleIngredient;
import org.bukkit.Material;
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
class CauldronDataTypeTest {
    @MockBukkitInject
    ServerMock server;
    private @NotNull WorldMock world;
    private Database database;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        this.world = server.addSimpleWorld("hello!");
        TheBrewingProject theBrewingProject = MockBukkit.load(TheBrewingProject.class);
        this.database = theBrewingProject.getDatabase();
        this.database.init();
    }

    @Test
    void checkPersistence() throws IOException, SQLException {
        Cauldron cauldron = new Cauldron(Map.of(new SimpleIngredient(Material.OAK_PLANKS), 10), world.getBlockAt(0, 0, 0), 103);
        database.insertValue(CauldronDataType.DATA_TYPE, cauldron);
        List<Cauldron> cauldrons = database.retrieveAll(CauldronDataType.DATA_TYPE, world);
        assertEquals(1, cauldrons.size());
        Cauldron retrievedCauldron = cauldrons.get(0);
        assertEquals(cauldron.getIngredients(), retrievedCauldron.getIngredients());
        assertEquals(cauldron.getBlock(), retrievedCauldron.getBlock());
        assertEquals(cauldron.getBrewStart(), retrievedCauldron.getBrewStart());
        Cauldron updatedValue =  new Cauldron(Map.of(new SimpleIngredient(Material.OAK_PLANKS), 11), world.getBlockAt(0, 0, 0), 104);
        database.updateValue(CauldronDataType.DATA_TYPE, updatedValue);
        List<Cauldron> updatedCauldrons = database.retrieveAll(CauldronDataType.DATA_TYPE, world);
        assertEquals(1, updatedCauldrons.size());
        assertEquals(104, updatedCauldrons.get(0).getBrewStart());
        assertEquals(11, updatedCauldrons.get(0).getIngredients().get(new SimpleIngredient(Material.OAK_PLANKS)));
        database.remove(CauldronDataType.DATA_TYPE, cauldron);
        assertEquals(0, database.retrieveAll(CauldronDataType.DATA_TYPE, world).size());
    }
}