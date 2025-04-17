package dev.jsinco.brewery.bukkit.breweries;

import com.google.gson.JsonParser;
import dev.jsinco.brewery.brew.Brew;
import dev.jsinco.brewery.bukkit.ingredient.BukkitIngredientManager;
import dev.jsinco.brewery.database.PersistenceException;
import dev.jsinco.brewery.database.sql.SqlStoredData;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.vector.BreweryLocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitMixerDataType implements SqlStoredData.Removable<BukkitMixer>, SqlStoredData.Updateable<BukkitMixer>,
        SqlStoredData.Insertable<BukkitMixer>, SqlStoredData.Findable<BukkitMixer, UUID> {

    public static final BukkitMixerDataType INSTANCE = new BukkitMixerDataType();

    @Override
    public List<BukkitMixer> find(UUID worldUuid, Connection connection) throws PersistenceException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/mixers_select_all.sql"))) {
            preparedStatement.setBytes(1, DecoderEncoder.asBytes(worldUuid));
            List<BukkitMixer> output = new ArrayList<>();
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                BreweryLocation location = new BreweryLocation(
                        resultSet.getInt("cauldron_x"),
                        resultSet.getInt("cauldron_y"),
                        resultSet.getInt("cauldron_z"),
                        worldUuid
                );
                output.add(new BukkitMixer(
                        location,
                        Brew.SERIALIZER.deserialize(JsonParser.parseString(resultSet.getString("brew")).getAsJsonArray(), BukkitIngredientManager.INSTANCE)
                ));
            }
            return output;
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void insert(BukkitMixer value, Connection connection) throws PersistenceException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/mixers_insert.sql"))) {
            BreweryLocation breweryLocation = value.position();
            preparedStatement.setInt(1, breweryLocation.x());
            preparedStatement.setInt(2, breweryLocation.y());
            preparedStatement.setInt(3, breweryLocation.z());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(breweryLocation.worldUuid()));
            preparedStatement.setString(5, Brew.SERIALIZER.serialize(value.getBrew()).toString());
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void remove(BukkitMixer toRemove, Connection connection) throws PersistenceException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/mixers_remove.sql"))) {
            BreweryLocation breweryLocation = toRemove.position();
            preparedStatement.setInt(1, breweryLocation.x());
            preparedStatement.setInt(2, breweryLocation.y());
            preparedStatement.setInt(3, breweryLocation.z());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(breweryLocation.worldUuid()));
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public void update(BukkitMixer newValue, Connection connection) throws PersistenceException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/mixers_update.sql"))) {
            BreweryLocation breweryLocation = newValue.position();
            preparedStatement.setString(1, Brew.SERIALIZER.serialize(newValue.getBrew()).toString());
            preparedStatement.setInt(2, breweryLocation.x());
            preparedStatement.setInt(3, breweryLocation.y());
            preparedStatement.setInt(4, breweryLocation.z());
            preparedStatement.setBytes(5, DecoderEncoder.asBytes(breweryLocation.worldUuid()));
            preparedStatement.execute();
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }
}
