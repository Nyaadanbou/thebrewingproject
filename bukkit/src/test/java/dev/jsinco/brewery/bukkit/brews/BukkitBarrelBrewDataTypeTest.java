package dev.jsinco.brewery.bukkit.brews;

import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.TheBrewingProject;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.bukkit.brew.BukkitBarrelBrewDataType;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.database.DatabaseDriver;
import dev.jsinco.brewery.bukkit.ingredient.SimpleIngredient;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;
import org.mockbukkit.mockbukkit.MockBukkitInject;
import org.mockbukkit.mockbukkit.ServerMock;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockBukkitExtension.class)
class BukkitBarrelBrewDataTypeTest {
    @MockBukkitInject
    ServerMock serverMock;

    private Database database;
    private World world;

    @BeforeEach
    void setUp() throws SQLException, IOException {
        TheBrewingProject theBrewingProject = MockBukkit.load(TheBrewingProject.class);
        this.database = theBrewingProject.getDatabase();
        this.world = serverMock.addSimpleWorld("hello_world!");
    }

    @Test
    void checkPersistence() throws SQLException, IOException {
        prepareBarrel();
        Brew<ItemStack> brew1 = new Brew<>(new PassedMoment(10), Map.of(new SimpleIngredient(Material.ACACIA_BUTTON), 3), new Interval(1010, 1010), 0, CauldronType.WATER, BarrelType.ACACIA);
        Brew<ItemStack> brew2 = new Brew<>(new PassedMoment(10), Map.of(new SimpleIngredient(Material.ACACIA_BUTTON), 3), new Interval(1010, 1010), 0, CauldronType.WATER, BarrelType.ACACIA);
        BukkitBarrelBrewDataType.BarrelContext barrelContext1 = new BukkitBarrelBrewDataType.BarrelContext(1, 2, 3, 0, world.getUID());
        BukkitBarrelBrewDataType.BarrelContext barrelContext2 = new BukkitBarrelBrewDataType.BarrelContext(1, 2, 3, 1, world.getUID());
        Pair<Brew<ItemStack>, BukkitBarrelBrewDataType.BarrelContext> data1 = new Pair<>(brew1, barrelContext1);
        Pair<Brew<ItemStack>, BukkitBarrelBrewDataType.BarrelContext> data2 = new Pair<>(brew2, barrelContext2);
        database.insertValue(BukkitBarrelBrewDataType.DATA_TYPE, data1);
        assertTrue(database.retrieveAll(BukkitBarrelBrewDataType.DATA_TYPE, world.getUID()).contains(data1));
        database.insertValue(BukkitBarrelBrewDataType.DATA_TYPE, data2);
        assertTrue(database.retrieveAll(BukkitBarrelBrewDataType.DATA_TYPE, world.getUID()).contains(data2));
        database.remove(BukkitBarrelBrewDataType.DATA_TYPE, data1);
        assertFalse(database.retrieveAll(BukkitBarrelBrewDataType.DATA_TYPE, world.getUID()).contains(data1));
    }

    private void prepareBarrel() throws SQLException {
        try (Connection connection = this.database.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/barrels_insert.sql"));
            preparedStatement.setInt(1, 0);
            preparedStatement.setInt(2, 0);
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 1);
            preparedStatement.setInt(5, 2);
            preparedStatement.setInt(6, 3);
            preparedStatement.setBytes(7, DecoderEncoder.asBytes(world.getUID()));
            preparedStatement.setString(8, "[1,2,3,4,5,6,7,8,9]");
            preparedStatement.setString(9, "test_format");
            preparedStatement.setString(10, BarrelType.ACACIA.key());
            preparedStatement.setInt(11, 9);
            preparedStatement.execute();
        }
    }
}