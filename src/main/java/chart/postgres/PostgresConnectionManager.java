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
            createSchema();
        }
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

    private void createSchema() throws SQLException {
        try (Connection conn = getConnection()) {
            createSchema(conn);
        }
    }

    private void createSchema(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();

        statement.executeUpdate("CREATE TABLE artists (" +
                                        "id varchar(256) CONSTRAINT artistid PRIMARY KEY," +
                                        "name varchar(256) NOT NULL," +
                                        "href varchar(512)," +
                                        "uri varchar(256)" +
                                        ");");

        statement.executeUpdate("CREATE TABLE tracks (" +
                                        "id varchar(256) CONSTRAINT trackid PRIMARY KEY," +
                                        "name varchar(256) NOT NULL," +
                                        "href varchar(512)," +
                                        "uri varchar(256)" +
                                        ");");

        statement.executeUpdate("CREATE TABLE trackArtists (" +
                                        "track_id varchar(256) REFERENCES tracks," +
                                        "artist_id varchar(256) REFERENCES artists" +
                                        ");");

        statement.executeUpdate("CREATE TABLE chart (" +
                                        "week int CONSTRAINT chartweek PRIMARY KEY," +
                                        "date date" +
                                        ");");

        statement.executeUpdate("CREATE TABLE chartEntries (" +
                                        "chart_week int REFERENCES chart," +
                                        "position int NOT NULL," +
                                        "track_id varchar(256) REFERENCES tracks," +
                                        "CONSTRAINT chart_position PRIMARY KEY(chart_week,position)," +
                                        "CONSTRAINT pospos CHECK (position > 0)" +
                                        ");");
    }
}