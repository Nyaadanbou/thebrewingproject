package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.database.FindableStoredData;
import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.UpdateableStoredData;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.recipes.ingredient.IngredientManager;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.PassedMoment;
import dev.jsinco.brewery.util.vector.BreweryLocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class BarrelBrewDataType<I> implements
        InsertableStoredData<Pair<Brew<I>, BarrelBrewDataType.BarrelContext>>, RemovableStoredData<Pair<Brew<I>, BarrelBrewDataType.BarrelContext>>,
        FindableStoredData<Pair<Brew<I>, Integer>, BreweryLocation>, UpdateableStoredData<Pair<Brew<I>, BarrelBrewDataType.BarrelContext>> {
    protected BarrelBrewDataType() {
    }

    @Override
    public void insert(Pair<Brew<I>, BarrelContext> value, Connection connection) throws SQLException {
        String statementString = FileUtil.readInternalResource("/database/generic/barrel_brews_insert.sql");
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementString)) {
            BarrelContext context = value.second();
            Brew<I> brew = value.first();
            preparedStatement.setInt(1, context.signX);
            preparedStatement.setInt(2, context.signY);
            preparedStatement.setInt(3, context.signZ);
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(context.worldUuid));
            preparedStatement.setInt(5, context.inventoryPos);
            preparedStatement.setInt(6, brew.distillRuns());
            preparedStatement.setString(7, brew.cauldronType().key().toString());
            preparedStatement.setLong(8, brew.brewTime().moment());
            preparedStatement.setLong(9, ((Interval) brew.aging()).start());
            preparedStatement.setString(10, Ingredient.ingredientsToJson(brew.ingredients()));
            preparedStatement.setString(11, brew.barrelType().key().toString());
            preparedStatement.execute();
        }
    }

    @Override
    public void remove(Pair<Brew<I>, BarrelContext> toRemove, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/barrel_brews_remove.sql"))) {
            BarrelContext context = toRemove.second();
            preparedStatement.setInt(1, context.signX);
            preparedStatement.setInt(2, context.signY);
            preparedStatement.setInt(3, context.signZ);
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(context.worldUuid));
            preparedStatement.setInt(5, context.inventoryPos);
            preparedStatement.execute();
        }
    }

    private Brew<I> brewFromResultSet(ResultSet resultSet) throws SQLException {
        long agingStart = resultSet.getLong("aging_start");
        return new Brew<>(new PassedMoment(resultSet.getLong("brew_time")), Ingredient.ingredientsFromJson(resultSet.getString("ingredients_json"), getIngredientManager()),
                new Interval(agingStart, agingStart), resultSet.getInt("distillery_runs"), Registry.CAULDRON_TYPE.get(resultSet.getString("cauldron_type")),
                Registry.BARREL_TYPE.get(resultSet.getString("barrel_type")));
    }

    protected abstract IngredientManager<I> getIngredientManager();

    private BarrelContext contextFromResultSet(ResultSet resultSet) throws SQLException {
        return new BarrelContext(resultSet.getInt("sign_x"), resultSet.getInt("sign_y"), resultSet.getInt("sign_z"), resultSet.getInt("pos"), DecoderEncoder.asUuid(resultSet.getBytes("world_uuid")));
    }

    @Override
    public List<Pair<Brew<I>, Integer>> find(BreweryLocation signLocation, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/barrel_brews_find.sql"))) {
            preparedStatement.setInt(1, signLocation.x());
            preparedStatement.setInt(2, signLocation.y());
            preparedStatement.setInt(3, signLocation.z());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(signLocation.worldUuid()));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Pair<Brew<I>, Integer>> output = new ArrayList<>();
            while (resultSet.next()) {
                output.add(new Pair<>(brewFromResultSet(resultSet), resultSet.getInt("pos")));
            }
            return output;
        }
    }

    @Override
    public void update(Pair<Brew<I>, BarrelContext> newValue, Connection connection) throws SQLException {
        String statementString = FileUtil.readInternalResource("/database/generic/barrel_brews_update.sql");
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementString)) {
            BarrelContext context = newValue.second();
            Brew<I> brew = newValue.first();
            preparedStatement.setString(1, brew.barrelType().key().toString());
            preparedStatement.setString(2, brew.cauldronType().key().toString());
            preparedStatement.setLong(3, brew.brewTime().moment());
            preparedStatement.setLong(4, ((Interval) brew.aging()).start());
            preparedStatement.setString(5, Ingredient.ingredientsToJson(brew.ingredients()));
            preparedStatement.setInt(6, brew.distillRuns());
            preparedStatement.setInt(7, context.signX);
            preparedStatement.setInt(8, context.signY);
            preparedStatement.setInt(9, context.signZ);
            preparedStatement.setBytes(10, DecoderEncoder.asBytes(context.worldUuid));
            preparedStatement.setInt(11, context.inventoryPos);
            preparedStatement.execute();
        }
    }

    public record BarrelContext(int signX, int signY, int signZ, int inventoryPos, UUID worldUuid) {
    }
}
