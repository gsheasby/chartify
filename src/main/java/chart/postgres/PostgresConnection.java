package chart.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.stream.Collectors;

import com.wrapper.spotify.models.SimpleArtist;

public class PostgresConnection {
    private final PostgresConfig config;

    public PostgresConnection(PostgresConfig config) {
        this.config = config;
    }

    public void setupSchema() throws ClassNotFoundException, SQLException {
        Class.forName("org.postgresql.Driver");

        boolean dbWasCreated = ensureDatabaseExists();
        if (dbWasCreated) {
            createSchema();
        }
    }

    // TODO does this abstraction make sense?
    // TODO better exception handling
    public void saveArtists(Set<SimpleArtist> artists) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO artists (id, href, name, uri) " +
                    "VALUES " + getFields(artists) +
                    " ON CONFLICT DO NOTHING";

            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert artists!", e);
        }
    }

    private String getFields(Set<SimpleArtist> artists) {
        return artists.stream().map(this::getFields).collect(Collectors.joining(", "));
    }

    private String getFields(SimpleArtist artist) {
        return String.format("('%s', '%s', '%s', '%s')",
                             artist.getId(),
                             artist.getHref(),
                             artist.getName(),
                             artist.getUri());
    }

    private boolean ensureDatabaseExists() throws SQLException {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:postgresql://localhost/",
                config.user(),
                config.password())) {
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
        try (Connection conn = getConnection())
        {
            createSchema(conn);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:postgresql://localhost/" + config.dbName(),
                config.user(),
                config.password());
    }

    private void createSchema(Connection conn) throws SQLException {
        Statement statement = conn.createStatement();

        statement.executeUpdate("CREATE TABLE artists (" +
                                        "id varchar(256) CONSTRAINT artistid PRIMARY KEY," +
                                        "href varchar(512)," +
                                        "name varchar(256) NOT NULL," +
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
