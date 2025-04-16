package dev.jsinco.brewery.database.sql;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.jsinco.brewery.database.*;
import dev.jsinco.brewery.util.FileUtil;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class Database implements PersistenceHandler<Connection> {

    private static final int BREWERY_DATABASE_VERSION = 0;
    private final DatabaseDriver driver;
    private HikariDataSource hikariDataSource;

    public Database(DatabaseDriver databaseDriver) {
        this.driver = databaseDriver;
    }

    public void init(File dataFolder) throws IOException, SQLException {
        HikariConfig config = switch (driver) {
            case SQLITE -> getHikariConfigForSqlite(dataFolder);
            default -> throw new UnsupportedOperationException("Currently not implemented");
        };
        this.hikariDataSource = new HikariDataSource(config);
        try (Connection connection = hikariDataSource.getConnection()) {
            createTables(connection);
        }
    }

    public Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    private static @NotNull HikariConfig getHikariConfigForSqlite(File dataFolder) throws IOException {
        File databaseFile = new File(dataFolder, "brewery.db");
        if (!databaseFile.exists() && !databaseFile.getParentFile().mkdirs() && !databaseFile.createNewFile()) {
            throw new IOException("Could not create file or dirs");
        }
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setPoolName("SQLiteConnectionPool");
        hikariConfig.setDriverClassName("org.sqlite.JDBC");
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + databaseFile);
        return hikariConfig;
    }

    private void createTables(Connection connection) throws SQLException {
        for (String statement : FileUtil.readInternalResource("/database/" + driver.name().toLowerCase(Locale.ROOT) + "/create_all_tables.sql").split(";")) {
            connection.prepareStatement(statement + ";").execute();
        }
        ResultSet resultSet = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/get_version.sql")).executeQuery();
        resultSet.next();
        if (resultSet.getInt("version") != BREWERY_DATABASE_VERSION) {
            // Refactor whenever that is needed
        }
        PreparedStatement preparedStatement = connection.prepareStatement(FileUtil.readInternalResource("/database/generic/set_version.sql"));
        preparedStatement.setInt(1, BREWERY_DATABASE_VERSION);
        preparedStatement.execute();
    }

    @Override
    public <T> List<T> retrieveAll(RetrievableStoredData<T, Connection> dataType) throws PersistenceException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            return dataType.retrieveAll(connection);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public <T> void remove(RemovableStoredData<T, Connection> dataType, T toRemove) throws PersistenceException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            dataType.remove(toRemove, connection);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public <T> void updateValue(UpdateableStoredData<T, Connection> dataType, T newValue) throws PersistenceException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            dataType.update(newValue, connection);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public <T> void updateValues(UpdateableStoredData<T, Connection> dataType, Collection<T> newValues) throws PersistenceException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            for (T value : newValues) {
                dataType.update(value, connection);
            }
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public <T> void insertValue(InsertableStoredData<T, Connection> dataType, T value) throws PersistenceException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            dataType.insert(value, connection);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public <T, U> List<T> find(FindableStoredData<T, U, Connection> dataType, U searchObject) throws PersistenceException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            return dataType.find(searchObject, connection);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public <T> T getSingleton(SingletonStoredData<T, Connection> dataType) throws PersistenceException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            return dataType.value(connection);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }

    @Override
    public <T> void setSingleton(SingletonStoredData<T, Connection> dataType, T t) throws PersistenceException {
        if (hikariDataSource == null) {
            throw new IllegalStateException("Not initialized");
        }
        try (Connection connection = hikariDataSource.getConnection()) {
            dataType.update(t, connection);
        } catch (SQLException e) {
            throw new PersistenceException(e);
        }
    }
}
