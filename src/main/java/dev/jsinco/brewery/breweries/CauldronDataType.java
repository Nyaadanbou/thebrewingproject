package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.RetrievableStoredData;
import dev.jsinco.brewery.database.UpdateableStoredData;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.vector.BreweryLocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class CauldronDataType<I> implements RetrievableStoredData<Cauldron<I>>, InsertableStoredData<Cauldron<I>>, UpdateableStoredData<Cauldron<I>>, RemovableStoredData<Cauldron<I>> {

    @Override
    public void insert(Cauldron<I> value, Connection connection) throws SQLException {
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
    public void update(Cauldron<I> newValue, Connection connection) throws SQLException {
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
    public void remove(Cauldron<I> toRemove, Connection connection) throws SQLException {
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
    public List<Cauldron<I>> retrieveAll(Connection connection, UUID world) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/cauldrons_select_all.sql"))) {
            preparedStatement.setBytes(1, DecoderEncoder.asBytes(world));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Cauldron<I>> cauldrons = new ArrayList<>();
            while (resultSet.next()) {
                int x = resultSet.getInt("cauldron_x");
                int y = resultSet.getInt("cauldron_y");
                int z = resultSet.getInt("cauldron_z");
                long brewStart = resultSet.getLong("brew_start");
                Map<Ingredient<I>, Integer> ingredients = Ingredient.ingredientsFromJson(resultSet.getString("ingredients_json"), getIngredientManager());
                cauldrons.add(newCauldron(new BreweryLocation(x, y, z, world), ingredients, brewStart));
            }
            return cauldrons;
        }
    }

    protected abstract IngredientManager<I> getIngredientManager();

    protected abstract Cauldron<I> newCauldron(BreweryLocation location, Map<Ingredient<I>, Integer> ingredients, long brewStart);
}
