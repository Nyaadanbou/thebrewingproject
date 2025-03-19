package dev.jsinco.brewery.bukkit.brew;

import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.bukkit.breweries.BukkitDistillery;
import dev.jsinco.brewery.bukkit.breweries.BukkitDistilleryDataType;
import dev.jsinco.brewery.bukkit.ingredient.SimpleIngredient;
import dev.jsinco.brewery.bukkit.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.moment.PassedMoment;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.joml.Matrix3d;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.MockBukkitInject;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockBukkitExtension.class)
class BukkitDistilleryBrewDataTypeTest {
    @MockBukkitInject
    ServerMock serverMock;

    private Database database;
    private World world;
    private TheBrewingProject theBrewingProject;

    @BeforeEach
    void setUp() {
        this.theBrewingProject = MockBukkit.load(TheBrewingProject.class);
        this.database = theBrewingProject.getDatabase();
        this.world = serverMock.addSimpleWorld("hello_world!");
    }

    @Test
    void checkPersistence() throws SQLException, IOException {
        BukkitDistillery bukkitDistillery = prepareDistillery();
        BreweryLocation searchObject = bukkitDistillery.getStructure().getUnique();
        Brew<ItemStack> brew1 = new Brew<>(new PassedMoment(10), Map.of(new SimpleIngredient(Material.ACACIA_BUTTON), 3), null, 0, CauldronType.WATER, null);
        Brew<ItemStack> brew2 = new Brew<>(new PassedMoment(10), Map.of(new SimpleIngredient(Material.ACACIA_BUTTON), 3), null, 2, CauldronType.WATER, null);
        BukkitDistilleryBrewDataType.DistilleryContext distilleryContext1 = new BukkitDistilleryBrewDataType.DistilleryContext(searchObject.x(), searchObject.y(), searchObject.z(), searchObject.worldUuid(), 0, false);
        BukkitDistilleryBrewDataType.DistilleryContext distilleryContext2 = new BukkitDistilleryBrewDataType.DistilleryContext(searchObject.x(), searchObject.y(), searchObject.z(), searchObject.worldUuid(), 0, true);
        Pair<Brew<ItemStack>, BukkitDistilleryBrewDataType.DistilleryContext> data1 = new Pair<>(brew1, distilleryContext1);
        Pair<Brew<ItemStack>, BukkitDistilleryBrewDataType.DistilleryContext> data2 = new Pair<>(brew2, distilleryContext2);
        database.insertValue(BukkitDistilleryBrewDataType.INSTANCE, data1);
        assertTrue(database.find(BukkitDistilleryBrewDataType.INSTANCE, searchObject).contains(data1));
        database.insertValue(BukkitDistilleryBrewDataType.INSTANCE, data2);
        assertTrue(database.find(BukkitDistilleryBrewDataType.INSTANCE, searchObject).contains(data2));
        assertTrue(database.find(BukkitDistilleryBrewDataType.INSTANCE, searchObject).contains(data1));
        database.remove(BukkitDistilleryBrewDataType.INSTANCE, data1);
        assertFalse(database.find(BukkitDistilleryBrewDataType.INSTANCE, searchObject).contains(data1));
    }

    private BukkitDistillery prepareDistillery() throws SQLException {
        BukkitDistillery output = new BukkitDistillery(new PlacedBreweryStructure<>(theBrewingProject.getStructureRegistry().getStructure("bamboo_distillery").get(), new Matrix3d(), new Location(world, 3, 3, 3)));
        database.insertValue(BukkitDistilleryDataType.INSTANCE, output);
        return output;
    }
}