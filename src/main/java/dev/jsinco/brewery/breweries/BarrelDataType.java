package dev.jsinco.brewery.breweries;

import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.brews.BarrelBrewDataType;
import dev.jsinco.brewery.brews.Brew;
import dev.jsinco.brewery.database.InsertableStoredData;
import dev.jsinco.brewery.database.RemovableStoredData;
import dev.jsinco.brewery.database.RetrievableStoredData;
import dev.jsinco.brewery.structure.BreweryStructure;
import dev.jsinco.brewery.structure.PlacedBreweryStructure;
import dev.jsinco.brewery.util.*;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.joml.Matrix3d;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BarrelDataType implements RetrievableStoredData<Barrel>, RemovableStoredData<Barrel>, InsertableStoredData<Barrel> {
    public static final BarrelDataType DATA_TYPE = new BarrelDataType();

    @Override
    public void insert(Barrel value, Connection connection) throws SQLException {
        PlacedBreweryStructure placedStructure = value.getStructure().get();
        BreweryStructure structure = placedStructure.getStructure();
        Location origin = placedStructure.getWorldOrigin();
        UUID worldUuid = value.getWorld().getUID();
        Location signLocation = value.getSignLocation();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/barrels_insert.sql"))) {
            preparedStatement.setInt(1, origin.getBlockX());
            preparedStatement.setInt(2, origin.getBlockY());
            preparedStatement.setInt(3, origin.getBlockZ());
            preparedStatement.setInt(4, signLocation.getBlockX());
            preparedStatement.setInt(5, signLocation.getBlockY());
            preparedStatement.setInt(6, signLocation.getBlockZ());
            preparedStatement.setBytes(7, DecoderEncoder.asBytes(worldUuid));
            preparedStatement.setString(8, DecoderEncoder.serializeTransformation(placedStructure.getTransformation()));
            preparedStatement.setString(9, structure.getName());
            preparedStatement.setString(10, value.getType().key().toString());
            preparedStatement.execute();
        }
        for (Pair<Brew, Integer> brew : value.getBrews()) {
            BarrelBrewDataType.BarrelContext context = new BarrelBrewDataType.BarrelContext(signLocation.getBlockX(),
                    signLocation.getBlockY(), signLocation.getBlockZ(), brew.second(), signLocation.getWorld().getUID());
            TheBrewingProject.getInstance().getDatabase().insertValue(BarrelBrewDataType.DATA_TYPE, new Pair<>(brew.first(), context));
        }
    }

    @Override
    public void remove(Barrel toRemove, Connection connection) throws SQLException {
        UUID worldUuid = toRemove.getWorld().getUID();
        Location signLocation = toRemove.getSignLocation();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/barrels_remove.sql"))) {
            preparedStatement.setInt(1, signLocation.getBlockX());
            preparedStatement.setInt(2, signLocation.getBlockY());
            preparedStatement.setInt(3, signLocation.getBlockZ());
            preparedStatement.setBytes(4, DecoderEncoder.asBytes(worldUuid));
            preparedStatement.execute();
        }
    }

    @Override
    public List<Barrel> retrieveAll(Connection connection, World world) throws SQLException {
        List<Barrel> output = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/barrels_select_all.sql"))) {
            preparedStatement.setBytes(1, DecoderEncoder.asBytes(world.getUID()));
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Location worldOrigin = new Location(world, resultSet.getInt("origin_x"), resultSet.getInt("origin_y"), resultSet.getInt("origin_z"));
                Location signLocation = new Location(world, resultSet.getInt("sign_x"), resultSet.getInt("sign_y"), resultSet.getInt("sign_z"));
                Matrix3d transform = DecoderEncoder.deserializeTransformation(resultSet.getString("transformation"));
                String format = resultSet.getString("format");
                BarrelType type = Registry.BARREL_TYPE.get(NamespacedKey.fromString(resultSet.getString("barrel_type")));
                int size = resultSet.getInt("size");

                Optional<BreweryStructure> breweryStructureOptional = TheBrewingProject.getInstance().getStructureRegistry().getStructure(format);
                if (breweryStructureOptional.isEmpty()) {
                    Logging.warning("Could not find format '" + format + "' for brewery structure with sign pos: " + signLocation);
                    continue;
                }
                PlacedBreweryStructure structure = new PlacedBreweryStructure(breweryStructureOptional.get(), transform, worldOrigin);

                output.add(new Barrel(signLocation, structure, size, type));
            }
        }
        for (Barrel barrel : output) {
            barrel.setBrews(TheBrewingProject.getInstance().getDatabase().find(BarrelBrewDataType.DATA_TYPE, barrel.getSignLocation()));
        }
        return output;
    }
}
