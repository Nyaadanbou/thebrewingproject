package dev.jsinco.brewery.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.jsinco.brewery.TheBrewingProject;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class Database {

    private static final int BREWERY_DATABASE_VERSION = 0;
    private final DatabaseDriver driver;
    private HikariDataSource hikariDataSource;

    public Database(DatabaseDriver databaseDriver) {
        this.driver = databaseDriver;
    }

    public void init() throws IOException, SQLException {
        HikariConfig config = switch (driver) {
            case SQLITE -> getHikariConfigForSqlite();
            default -> throw new UnsupportedOperationException("Currently not implemented");
        };
        this.hikariDataSource = new HikariDataSource(config);
        try (Connection connection = hikariDataSource.getConnection()) {
            createTables(connection);
        }
    }

    private static @NotNull HikariConfig getHikariConfigForSqlite() throws IOException {
        File databaseFile = new File(TheBrewingProject.getInstance().getDataFolder(), "brewery.db");
        if (!databaseFile.exists() && !databaseFile.getParentFile().mkdirs() && !databaseFile.createNewFile()) {
            throw new IOException("Could not create file or dirs");
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("SQLiteConnectionPool");
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + databaseFile);
        return hikariConfig;
    }

    private void createTables(Connection connection) throws IOException, SQLException {
        try (InputStream inputStream = TheBrewingProject.class.getResourceAsStream("/database/" + driver.name().toLowerCase(Locale.ROOT) + "/create_all_tables.sql")) {
            byte[] bytes = inputStream.readAllBytes();
            for (String statement : new String(bytes, StandardCharsets.UTF_8).split(";")) {
                connection.prepareStatement(statement + ";").execute();
            }
        }
        try (InputStream inputStream = TheBrewingProject.class.getResourceAsStream("/database/generic/get_version.sql")) {
            byte[] bytes = inputStream.readAllBytes();
            ResultSet resultSet = connection.prepareStatement(new String(bytes, StandardCharsets.UTF_8)).executeQuery();
            resultSet.next();
            if (resultSet.getInt("version") != BREWERY_DATABASE_VERSION) {
                // Refactor whenever that is needed
            }
        }
        try (InputStream inputStream = TheBrewingProject.class.getResourceAsStream("/database/generic/set_version.sql")) {
            byte[] bytes = inputStream.readAllBytes();
            PreparedStatement preparedStatement = connection.prepareStatement(new String(bytes, StandardCharsets.UTF_8));
            preparedStatement.setInt(1, BREWERY_DATABASE_VERSION);
            preparedStatement.execute();
        }
    }

    public <T> List<T> retrieveAll(StoredDataType<T> dataType) throws SQLException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            return dataType.retrieveAll(connection);
        }
    }

    public <T> void remove(StoredDataType<T> dataType, T toRemove) throws SQLException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            dataType.remove(toRemove, connection);
        }
    }

    public <T> void updateValue(StoredDataType<T> dataType, T newValue) throws SQLException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            dataType.update(newValue, connection);
        }
    }

    public <T> void updateValues(StoredDataType<T> dataType, Collection<T> newValues) throws SQLException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            for (T value : newValues) {
                dataType.update(value, connection);
            }
        }
    }
}
