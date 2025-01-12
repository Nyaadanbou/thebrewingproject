package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.database.*;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.Interval;
import dev.jsinco.brewery.util.moment.PassedMoment;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BarrelBrewDataType implements RetrievableStoredData<Pair<Brew, BarrelBrewDataType.BarrelContext>>,
        InsertableStoredData<Pair<Brew, BarrelBrewDataType.BarrelContext>>, RemovableStoredData<Pair<Brew, BarrelBrewDataType.BarrelContext>>,
        FindableStoredData<Pair<Brew, Integer>, Location>, UpdateableStoredData<Pair<Brew, BarrelBrewDataType.BarrelContext>> {
    public static final BarrelBrewDataType DATA_TYPE = new BarrelBrewDataType();

    private BarrelBrewDataType() {
    }

    @Override
    public void insert(Pair<Brew, BarrelContext> value, Connection connection) throws SQLException {
        String statementString = FileUtil.readInternalResource("/database/generic/barrel_brews_insert.sql");
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementString)) {
            BarrelContext context = value.second();
            Brew brew = value.first();
            preparedStatement.setInt(1, context.signX);
            preparedStatement.setInt(2, context.signY);
            preparedStatement.setInt(3, context.signZ);
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(context.worldUuid));
            preparedStatement.setInt(5, context.inventoryPos);
            preparedStatement.setString(6, brew.barrelType().key().toString());
            preparedStatement.setString(7, brew.cauldronType().key().toString());
            preparedStatement.setLong(8, brew.brewTime().moment());
            preparedStatement.setLong(9, ((Interval) brew.aging()).start());
            preparedStatement.setString(10, Ingredient.ingredientsToJson(brew.ingredients()));
            preparedStatement.execute();
        }
    }

    @Override
    public void remove(Pair<Brew, BarrelContext> toRemove, Connection connection) throws SQLException {
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

    @Override
    public List<Pair<Brew, BarrelContext>> retrieveAll(Connection connection, World world) throws SQLException {
        List<Pair<Brew, BarrelContext>> output = new ArrayList<>();
        String statementString = FileUtil.readInternalResource("/database/generic/barrel_brews_select_all.sql");
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementString)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                output.add(new Pair<>(brewFromResultSet(resultSet), contextFromResultSet(resultSet)));
            }
        }
        return output;
    }

    private Brew brewFromResultSet(ResultSet resultSet) throws SQLException {
        long agingStart = resultSet.getLong("aging_start");
        return new Brew(new PassedMoment(resultSet.getLong("brew_time")), Ingredient.ingredientsFromJson(resultSet.getString("ingredients_json")),
                new Interval(agingStart, agingStart), 0, Registry.CAULDRON_TYPE.get(NamespacedKey.fromString(resultSet.getString("cauldron_type"))),
                Registry.BARREL_TYPE.get(NamespacedKey.fromString(resultSet.getString("barrel_type"))));
    }

    private BarrelContext contextFromResultSet(ResultSet resultSet) throws SQLException {
        return new BarrelContext(resultSet.getInt("sign_x"), resultSet.getInt("sign_y"), resultSet.getInt("sign_z"), resultSet.getInt("pos"), DecoderEncoder.asUuid(resultSet.getBytes("world_uuid")));
    }

    @Override
    public List<Pair<Brew, Integer>> find(Location signLocation, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/barrel_brews_find.sql"))) {
            preparedStatement.setInt(1, signLocation.getBlockX());
            preparedStatement.setInt(2, signLocation.getBlockY());
            preparedStatement.setInt(3, signLocation.getBlockZ());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(signLocation.getWorld().getUID()));
            ResultSet resultSet = preparedStatement.executeQuery();
            List<Pair<Brew, Integer>> output = new ArrayList<>();
            while (resultSet.next()) {
                output.add(new Pair<>(brewFromResultSet(resultSet), resultSet.getInt("pos")));
            }
            return output;
        }
    }

    @Override
    public void update(Pair<Brew, BarrelContext> newValue, Connection connection) throws SQLException {
        String statementString = FileUtil.readInternalResource("/database/generic/barrel_brews_update.sql");
        try (PreparedStatement preparedStatement = connection.prepareStatement(statementString)) {
            BarrelContext context = newValue.second();
            Brew brew = newValue.first();
            preparedStatement.setString(1, brew.barrelType().key().toString());
            preparedStatement.setString(2, brew.cauldronType().key().toString());
            preparedStatement.setLong(3, brew.brewTime().moment());
            preparedStatement.setLong(4, ((Interval) brew.aging()).start());
            preparedStatement.setString(5, Ingredient.ingredientsToJson(brew.ingredients()));
            preparedStatement.setInt(6, context.signX);
            preparedStatement.setInt(7, context.signY);
            preparedStatement.setInt(8, context.signZ);
            preparedStatement.setBytes(9, DecoderEncoder.asBytes(context.worldUuid));
            preparedStatement.setInt(10, context.inventoryPos);
            preparedStatement.execute();
        }
    }

    public record BarrelContext(int signX, int signY, int signZ, int inventoryPos, UUID worldUuid) {
    }
}
