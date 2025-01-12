package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.RetrievableStoredData;
import dev.jsinco.brewery.database.UpdateableStoredData;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CauldronDataType implements RetrievableStoredData<Cauldron>, InsertableStoredData<Cauldron>, UpdateableStoredData<Cauldron>, RemovableStoredData<Cauldron> {
    public static final CauldronDataType DATA_TYPE = new CauldronDataType();

    @Override
    public void insert(Cauldron value, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_insert.sql"))) {
            Location location = value.getBlock().getLocation();
            preparedStatement.setInt(1, location.getBlockX());
            preparedStatement.setInt(2, location.getBlockY());
            preparedStatement.setInt(3, location.getBlockZ());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(location.getWorld().getUID()));
            preparedStatement.setLong(5, value.getBrewStart());
            preparedStatement.setString(6, Ingredient.ingredientsToJson(value.getIngredients()));
            preparedStatement.execute();
        }
    }

    @Override
    public void update(Cauldron newValue, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_update.sql"))) {
            Location location = newValue.getBlock().getLocation();
            preparedStatement.setLong(1, newValue.getBrewStart());
            preparedStatement.setString(2, Ingredient.ingredientsToJson(newValue.getIngredients()));
            preparedStatement.setInt(3, location.getBlockX());
            preparedStatement.setInt(4, location.getBlockY());
            preparedStatement.setInt(5, location.getBlockZ());
            preparedStatement.setBytes(6, DecoderEncoder.asBytes(location.getWorld().getUID()));
            preparedStatement.execute();
        }
    }

    @Override
    public void remove(Cauldron toRemove, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_remove.sql"))) {
            Location location = toRemove.getBlock().getLocation();
            preparedStatement.setInt(1, location.getBlockX());
            preparedStatement.setInt(2, location.getBlockY());
            preparedStatement.setInt(3, location.getBlockZ());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(location.getWorld().getUID()));
            preparedStatement.execute();
        }
    }

    @Override
    public List<Cauldron> retrieveAll(Connection connection, World world) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_select_all.sql"))) {
            preparedStatement.setBytes(1, DecoderEncoder.asBytes(world.getUID()));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Cauldron> cauldrons = new ArrayList<>();
            while (resultSet.next()) {
                int x = resultSet.getInt("cauldron_x");
                int y = resultSet.getInt("cauldron_y");
                int z = resultSet.getInt("cauldron_z");
                long brewStart = resultSet.getLong("brew_start");
                Map<Ingredient, Integer> ingredients = Ingredient.ingredientsFromJson(resultSet.getString("ingredients_json"));
                cauldrons.add(new Cauldron(ingredients, new Location(world, x, y, z).getBlock(), brewStart));
            }
            return cauldrons;
        }
    }
}
