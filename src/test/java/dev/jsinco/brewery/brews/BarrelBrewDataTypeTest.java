package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.breweries.BarrelType;
import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.database.Database;
import dev.jsinco.brewery.database.DatabaseDriver;
import dev.jsinco.brewery.recipes.ingredient.SimpleIngredient;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.bukkit.Material;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockbukkit.mockbukkit.MockBukkit;
import org.mockbukkit.mockbukkit.MockBukkitExtension;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockBukkitExtension.class)
class BarrelBrewDataTypeTest {

    private Database database;
    UUID worldUuid = UUID.randomUUID();

    @BeforeEach
    void setUp() throws SQLException, IOException {
        MockBukkit.load(TheBrewingProject.class);
        this.database = new Database(DatabaseDriver.SQLITE);
        this.database.init();
    }

    @Test
    void checkPersistence() throws SQLException, IOException {
        prepareBarrel();
        Brew brew1 = new Brew(new PassedMoment(10), Map.of(new SimpleIngredient(Material.ACACIA_BUTTON), 3), new Interval(1010, 1010), 0, CauldronType.WATER, BarrelType.ACACIA);
        Brew brew2 = new Brew(new PassedMoment(10), Map.of(new SimpleIngredient(Material.ACACIA_BUTTON), 3), new Interval(1010, 1010), 0, CauldronType.WATER, BarrelType.ACACIA);
        BarrelBrewDataType.BarrelContext barrelContext1 = new BarrelBrewDataType.BarrelContext(1, 2, 3, 0, worldUuid);
        BarrelBrewDataType.BarrelContext barrelContext2 = new BarrelBrewDataType.BarrelContext(1, 2, 3, 1, worldUuid);
        Pair<Brew, BarrelBrewDataType.BarrelContext> data1 = new Pair<>(brew1, barrelContext1);
        Pair<Brew, BarrelBrewDataType.BarrelContext> data2 = new Pair<>(brew2, barrelContext2);
        database.insertValue(BarrelBrewDataType.DATA_TYPE, data1);
        assertTrue(database.retrieveAll(BarrelBrewDataType.DATA_TYPE).contains(data1));
        database.insertValue(BarrelBrewDataType.DATA_TYPE, data2);
        assertTrue(database.retrieveAll(BarrelBrewDataType.DATA_TYPE).contains(data2));
        database.remove(BarrelBrewDataType.DATA_TYPE, data1);
        assertFalse(database.retrieveAll(BarrelBrewDataType.DATA_TYPE).contains(data1));
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
            preparedStatement.setBytes(7, DecoderEncoder.asBytes(worldUuid));
            preparedStatement.setDouble(8, 0);
            preparedStatement.setDouble(9, 0);
            preparedStatement.setDouble(10, 0);
            preparedStatement.setDouble(11, 0);
            preparedStatement.setBoolean(12, false);
            preparedStatement.setString(13, "test_format");
            preparedStatement.setString(14, BarrelType.ACACIA.key().toString());
            preparedStatement.execute();
        }
    }
}