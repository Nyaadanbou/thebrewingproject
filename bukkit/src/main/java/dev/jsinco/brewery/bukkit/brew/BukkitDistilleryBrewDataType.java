package dev.jsinco.brewery.bukkit.brew;

import dev.jsinco.brewery.breweries.CauldronType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.database.FindableStoredData;
import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.UpdateableStoredData;
import dev.jsinco.brewery.recipes.ingredient.Ingredient;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.Registry;
import dev.jsinco.brewery.util.moment.PassedMoment;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.inventory.ItemStack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class BukkitDistilleryBrewDataType implements
        InsertableStoredData<Pair<Brew<ItemStack>, BukkitDistilleryBrewDataType.DistilleryContext>>, RemovableStoredData<Pair<Brew<ItemStack>, BukkitDistilleryBrewDataType.DistilleryContext>>,
        FindableStoredData<Pair<Brew<ItemStack>, BukkitDistilleryBrewDataType.DistilleryContext>, BreweryLocation>, UpdateableStoredData<Pair<Brew<ItemStack>, BukkitDistilleryBrewDataType.DistilleryContext>> {

    public static final BukkitDistilleryBrewDataType INSTANCE = new BukkitDistilleryBrewDataType();

    @Override
    public List<Pair<Brew<ItemStack>, DistilleryContext>> find(BreweryLocation searchObject, Connection connection) throws SQLException {
        List<Pair<Brew<ItemStack>, DistilleryContext>> output = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/distillery_brews_find.sql"))) {
            preparedStatement.setInt(1, searchObject.x());
            preparedStatement.setInt(2, searchObject.y());
            preparedStatement.setInt(3, searchObject.z());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(searchObject.worldUuid()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int pos = resultSet.getInt("pos");
                boolean isDistillate = resultSet.getBoolean("is_distillate");
                CauldronType cauldronType = Registry.CAULDRON_TYPE.get(resultSet.getString("cauldron_type"));
                int brewTime = resultSet.getInt("brew_time");
                int runs = resultSet.getInt("distillery_runs");
                Map<Ingredient<ItemStack>, Integer> ingredients = Ingredient.ingredientsFromJson(resultSet.getString("ingredients_json"), BukkitIngredientManager.INSTANCE);
                DistilleryContext distilleryContext = new DistilleryContext(searchObject.x(), searchObject.y(), searchObject.z(), searchObject.worldUuid(), pos, isDistillate);
                Brew<ItemStack> brew = new Brew<>(new PassedMoment(brewTime), ingredients, null, runs, cauldronType, null);
                output.add(new Pair<>(brew, distilleryContext));
            }
        }
        return output;
    }

    @Override
    public void insert(Pair<Brew<ItemStack>, DistilleryContext> value, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/distillery_brews_insert.sql"))) {
            DistilleryContext distilleryContext = value.second();
            Brew<ItemStack> brew = value.first();
            preparedStatement.setInt(1, distilleryContext.uniqueX());
            preparedStatement.setInt(2, distilleryContext.uniqueY());
            preparedStatement.setInt(3, distilleryContext.uniqueZ());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(distilleryContext.worldUuid()));
            preparedStatement.setInt(5, distilleryContext.inventoryPos());
            preparedStatement.setBoolean(6, distilleryContext.distillate());
            preparedStatement.setString(7, brew.cauldronType().key());
            preparedStatement.setLong(8, brew.brewTime().moment());
            preparedStatement.setInt(9, brew.distillRuns());
            preparedStatement.setString(10, Ingredient.ingredientsToJson(brew.ingredients()));
            preparedStatement.execute();
        }
    }

    @Override
    public void remove(Pair<Brew<ItemStack>, DistilleryContext> toRemove, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/distillery_brews_remove.sql"))) {
            DistilleryContext distilleryContext = toRemove.second();
            preparedStatement.setInt(1, distilleryContext.uniqueX());
            preparedStatement.setInt(2, distilleryContext.uniqueY());
            preparedStatement.setInt(3, distilleryContext.uniqueZ());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(distilleryContext.worldUuid()));
            preparedStatement.setInt(5, distilleryContext.inventoryPos());
            preparedStatement.setBoolean(6, distilleryContext.distillate());
            preparedStatement.execute();
        }
    }

    @Override
    public void update(Pair<Brew<ItemStack>, DistilleryContext> newValue, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/distillery_brews_update.sql"))) {
            Brew<ItemStack> brew = newValue.first();
            DistilleryContext distilleryContext = newValue.second();
            preparedStatement.setString(1, brew.cauldronType().key());
            preparedStatement.setLong(2, brew.brewTime().moment());
            preparedStatement.setInt(3, brew.distillRuns());
            preparedStatement.setString(4, Ingredient.ingredientsToJson(brew.ingredients()));
            preparedStatement.setInt(5, distilleryContext.uniqueX());
            preparedStatement.setInt(6, distilleryContext.uniqueY());
            preparedStatement.setInt(7, distilleryContext.uniqueZ());
            preparedStatement.setBytes(8, DecoderEncoder.asBytes(distilleryContext.worldUuid()));
            preparedStatement.setInt(9, distilleryContext.inventoryPos());
            preparedStatement.setBoolean(10, distilleryContext.distillate());
            preparedStatement.execute();
        }
    }

    public record DistilleryContext(int uniqueX, int uniqueY, int uniqueZ, UUID worldUuid, int inventoryPos,
                                    boolean distillate) {

    }
}
