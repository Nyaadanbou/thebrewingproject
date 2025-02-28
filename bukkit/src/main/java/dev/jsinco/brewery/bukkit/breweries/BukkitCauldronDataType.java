package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.breweries.Cauldron;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.RetrievableStoredData;
import dev.jsinco.brewery.database.UpdateableStoredData;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BukkitCauldronDataType implements RetrievableStoredData<BukkitCauldron>, InsertableStoredData<BukkitCauldron>, UpdateableStoredData<BukkitCauldron>, RemovableStoredData<BukkitCauldron> {

    public static final BukkitCauldronDataType INSTANCE = new BukkitCauldronDataType();

    @Override
    public void insert(BukkitCauldron value, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_insert.sql"))) {
            BreweryLocation location = value.position();
            preparedStatement.setInt(1, location.x());
            preparedStatement.setInt(2, location.y());
            preparedStatement.setInt(3, location.z());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(location.worldUuid()));
            preparedStatement.setLong(5, value.brewStart());
            preparedStatement.setString(6, Ingredient.ingredientsToJson(value.ingredients()));
            preparedStatement.execute();
        }
    }

    @Override
    public void update(BukkitCauldron newValue, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_update.sql"))) {
            BreweryLocation location = newValue.position();
            preparedStatement.setLong(1, newValue.brewStart());
            preparedStatement.setString(2, Ingredient.ingredientsToJson(newValue.ingredients()));
            preparedStatement.setInt(3, location.x());
            preparedStatement.setInt(4, location.y());
            preparedStatement.setInt(5, location.z());
            preparedStatement.setBytes(6, DecoderEncoder.asBytes(location.worldUuid()));
            preparedStatement.execute();
        }
    }

    @Override
    public void remove(BukkitCauldron toRemove, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_remove.sql"))) {
            BreweryLocation location = toRemove.position();
            preparedStatement.setInt(1, location.x());
            preparedStatement.setInt(2, location.y());
            preparedStatement.setInt(3, location.z());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(location.worldUuid()));
            preparedStatement.execute();
        }
    }

    @Override
    public List<BukkitCauldron> retrieveAll(Connection connection, UUID world) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_select_all.sql"))) {
            preparedStatement.setBytes(1, DecoderEncoder.asBytes(world));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<BukkitCauldron> cauldrons = new ArrayList<>();
            while (resultSet.next()) {
                int x = resultSet.getInt("cauldron_x");
                int y = resultSet.getInt("cauldron_y");
                int z = resultSet.getInt("cauldron_z");
                long brewStart = resultSet.getLong("brew_start");
                Map<Ingredient<ItemStack>, Integer> ingredients = Ingredient.ingredientsFromJson(resultSet.getString("ingredients_json"), BukkitIngredientManager.INSTANCE);
                cauldrons.add(new BukkitCauldron(ingredients, Bukkit.getWorld(world).getBlockAt(x, y, z), brewStart));
            }
            return cauldrons;
        }
    }
}
