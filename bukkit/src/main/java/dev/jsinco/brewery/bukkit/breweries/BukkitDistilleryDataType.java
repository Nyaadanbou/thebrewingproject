package dev.jsinco.brewery.bukkit.breweries;

import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.RetrievableStoredData;
import dev.jsinco.brewery.database.UpdateableStoredData;
import dev.jsinco.brewery.util.DecoderEncoder;
import dev.jsinco.brewery.util.FileUtil;
import dev.jsinco.brewery.util.vector.BreweryLocation;
import org.bukkit.Bukkit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BukkitDistilleryDataType implements RetrievableStoredData<BukkitDistillery>, InsertableStoredData<BukkitDistillery>, RemovableStoredData<BukkitDistillery>, UpdateableStoredData<BukkitDistillery> {

    public static final BukkitDistilleryDataType INSTANCE = new BukkitDistilleryDataType();

    @Override
    public void insert(BukkitDistillery value, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/distilleries_insert.sql"))) {
            BreweryLocation location = value.getLocation();
            preparedStatement.setInt(1, location.x());
            preparedStatement.setInt(2, location.y());
            preparedStatement.setInt(3, location.z());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(location.worldUuid()));
            preparedStatement.setLong(5, value.getStartTime());
            preparedStatement.execute();
        }
    }

    @Override
    public void remove(BukkitDistillery toRemove, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/distilleries_remove.sql"))) {
            BreweryLocation location = toRemove.getLocation();
            preparedStatement.setInt(1, location.x());
            preparedStatement.setInt(2, location.y());
            preparedStatement.setInt(3, location.z());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(location.worldUuid()));
            preparedStatement.execute();
        }
    }

    @Override
    public List<BukkitDistillery> retrieveAll(Connection connection, UUID world) throws SQLException {
        List<BukkitDistillery> output = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/distilleries_select_all.sql"))) {
            preparedStatement.setBytes(1, DecoderEncoder.asBytes(world));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int x = resultSet.getInt("x");
                int y = resultSet.getInt("y");
                int z = resultSet.getInt("z");
                int startTime = resultSet.getInt("start_time");

                output.add(new BukkitDistillery(Bukkit.getWorld(world).getBlockAt(x, y, z), startTime));
            }
        }
        return output;
    }

    @Override
    public void update(BukkitDistillery newValue, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/distilleries_update.sql"))) {
            long startTime = newValue.getStartTime();
            BreweryLocation location = newValue.getLocation();
            preparedStatement.setLong(1, startTime);
            preparedStatement.setInt(2, location.x());
            preparedStatement.setInt(3, location.y());
            preparedStatement.setInt(4, location.z());
            preparedStatement.setBytes(5, DecoderEncoder.asBytes(location.worldUuid()));
            preparedStatement.execute();
        }
    }
}
