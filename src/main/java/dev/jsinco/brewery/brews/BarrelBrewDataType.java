package dev.jsinco.brewery.brews;

import dev.jsinco.brewery.TheBrewingProject;
import dev.jsinco.brewery.database.StoredDataType;
import dev.jsinco.brewery.util.Pair;
import dev.jsinco.brewery.util.moment.PassedMoment;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BarrelBrewDataType implements StoredDataType<Pair<Brew, BarrelBrewDataType.BarrelContext>> {
    @Override
    public void update(Pair<Brew, BarrelContext> newValue, Connection connection) throws SQLException {

    }

    @Override
    public void remove(Pair<Brew, BarrelContext> toRemove, Connection connection) throws SQLException {

    }

    @Override
    public List<Pair<Brew, BarrelContext>> retrieveAll(Connection connection) throws SQLException, IOException {
        List<Pair<Brew, BarrelContext>> output = new ArrayList<>();
        try (InputStream inputStream = TheBrewingProject.class.getResourceAsStream("database/generic/barrel_brews_select_all.sql")) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8))) {
                ResultSet resultSet = preparedStatement.executeQuery();
                while (resultSet.next()) {
                    output.add(new Pair<>(brewFromResultSet(resultSet), contextFromResultSet(resultSet)));
                }
            }
        }
        return output;
    }

    private Brew brewFromResultSet(ResultSet resultSet) {
        return null;
    }

    private BarrelContext contextFromResultSet(ResultSet resultSet) throws SQLException {
        return new BarrelContext(resultSet.getInt("sign_x"), resultSet.getInt("sign_y"), resultSet.getInt("sign_z"), resultSet.getInt("pos"));
    }

    public record BarrelContext(int signX, int signY, int signZ, int inventoryPos) {
    }
}
