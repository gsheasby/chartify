package chart.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PostgresConnectionManager {
    final PostgresConfig config;

    public static PostgresConnectionManager create(PostgresConfig config) throws SQLException, ClassNotFoundException {
        PostgresConnectionManager manager = new PostgresConnectionManager(config);
        manager.setupSchema();
        return manager;
    }

    private PostgresConnectionManager(PostgresConfig config) {
        this.config = config;
    }

    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                getDatabaseUrl(),
                config.user(),
                config.password());
    }

    private String getDatabaseUrl() {
        return getPostgresUrl() + config.dbName();
    }

    private String getPostgresUrl() {
        return String.format("jdbc:postgresql://localhost:%d/", config.port());
    }

    private void setupSchema() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        boolean dbWasCreated = ensureDatabaseExists();
        if (dbWasCreated) {
            createInitialSchema();
        }

        updateSchema();
    }

    private boolean ensureDatabaseExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                getPostgresUrl(),
                config.user(),
                config.password()))
        {
            return createDatabaseIfNotExists(conn);
        }
    }

    private boolean createDatabaseIfNotExists(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();

        ResultSet resultSet = statement.executeQuery(String.format("SELECT 1 FROM pg_database WHERE datname = '%s'", config.dbName()));
        if (!resultSet.next()) {
            System.out.println("Creating db for user " + config.user());
            int output = statement.executeUpdate("CREATE DATABASE " + config.dbName());
            System.out.println("Create database returned " + output);
            return true;
        } else {
            System.out.println("Skipping creation - DB exists");
            return false;
        }
    }

    private void createInitialSchema() throws SQLException {
        try (Connection conn = getConnection()) {
            createInitialSchema(conn);
        }
    }

    private void createInitialSchema(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS artists (" +
                                        "id varchar(256) CONSTRAINT artistid PRIMARY KEY," +
                                        "name varchar(256) NOT NULL," +
                                        "href varchar(512)," +
                                        "uri varchar(256)," +
                                        "is_youtube boolean DEFAULT FALSE" +
                                        ");");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS tracks (" +
                                        "id varchar(256) CONSTRAINT trackid PRIMARY KEY," +
                                        "name varchar(256) NOT NULL," +
                                        "href varchar(512)," +
                                        "uri varchar(256)," +
                                        "is_youtube boolean DEFAULT FALSE" +
                                        ");");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS trackArtists (" +
                                        "track_id varchar(256) REFERENCES tracks," +
                                        "artist_id varchar(256) REFERENCES artists," +
                                        "CONSTRAINT track_artist PRIMARY KEY(track_id, artist_id)" +
                                        ");");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS chart (" +
                                        "week int CONSTRAINT chartweek PRIMARY KEY," +
                                        "date date" +
                                        ");");

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS chartEntries (" +
                                        "chart_week int REFERENCES chart," +
                                        "position int NOT NULL," +
                                        "track_id varchar(256) REFERENCES tracks," +
                                        "CONSTRAINT chart_position PRIMARY KEY(chart_week,position)," +
                                        "CONSTRAINT pospos CHECK (position > 0)" +
                                        ");");
    }

    private void updateSchema() throws SQLException {
        try (Connection conn = getConnection()) {
            updateSchema(conn);
        }
    }

    private void updateSchema(Connection conn) throws SQLException {
        // Create schema_version table if it doesn't exist
        Statement statement = conn.createStatement();

        statement.executeUpdate("CREATE TABLE IF NOT EXISTS schemaVersion (" +
                                        "key varchar(128)," +
                                        "version int);");

        int version = getSchemaVersion(statement);
        if (version < 2) {
            updateToVersionTwo(statement);
            System.out.println("Updated schema to version 2");
        } else {
            System.out.println("Database schema is up to date");
        }
    }

    private void updateToVersionTwo(Statement statement) throws SQLException {
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS yearEndChartEntries (" +
                                        "year int NOT NULL," +
                                        "position int NOT NULL," +
                                        "track_id varchar(256) REFERENCES tracks," +
                                        "CONSTRAINT yec_position PRIMARY KEY(year,position)," +
                                        "CONSTRAINT yec_year_pos CHECK (year > 0)," +
                                        "CONSTRAINT yec_pos_pos CHECK (position > 0)" +
                                        ");");

        statement.executeUpdate("INSERT INTO schemaVersion (key, version) VALUES ('version', 2)");
    }

    private int getSchemaVersion(Statement statement) throws SQLException {
        // Query version - if 1, then upgrade to 2
        ResultSet resultSet = statement.executeQuery("SELECT version FROM schemaVersion WHERE key = 'version'");
        if (!resultSet.next()) {
            return 1;
        } else {
            return resultSet.getInt("version");
        }
    }
}
