package chart.postgres;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import java.util.stream.Collectors;

import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

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
    // TODO - what if a new artist has an ID that isn't its spotify ID? Maybe do a lookup for artists when importing from CSV
    // TODO better exception handling
    public void saveArtists(Set<SimpleArtist> artists) {
        try (Connection conn = getConnection()) {
            String sql = "INSERT INTO artists (id, href, name, uri) " +
                    "VALUES " + getFieldsForArtists(artists) +
                    " ON CONFLICT DO NOTHING";

            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert artists!", e);
        }
    }

    public void saveTracks(Set<Track> tracks) {
        try (Connection conn = getConnection()) {
            // Save the tracks
            String sql = "INSERT INTO tracks (id, href, name, uri) " +
                    "VALUES " + getFieldsForTracks(tracks) +
                    " ON CONFLICT DO NOTHING";

            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);

            // Connect tracks to artists
            String insertTrackArtists = "INSERT INTO trackArtists (track_id, artist_id) " +
                    "VALUES " + getArtistsForTracks(tracks) +
                    " ON CONFLICT DO NOTHING";
            statement.executeUpdate(insertTrackArtists);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert tracks!", e);
        }
    }

    private String getArtistsForTracks(Set<Track> tracks) {
        return tracks.stream().map(this::getArtistsForTrack).collect(Collectors.joining(", "));
    }

    private String getArtistsForTrack(Track track) {
        return track.getArtists().stream()
                    .map(artist -> getTrackAndArtistIds(track, artist))
                    .collect(Collectors.joining(", "));
    }

    private String getTrackAndArtistIds(Track track, SimpleArtist artist) {
        return String.format("(%s, %s)", track.getId(), artist.getId());
    }

    private String getFieldsForTracks(Set<Track> tracks) {
        return tracks.stream().map(this::getFieldsForTrack).collect(Collectors.joining(", "));
    }

    private String getFieldsForTrack(Track track) {
        return String.format("('%s', '%s', '%s', '%s')",
                             track.getId(),
                             track.getName(),
                             track.getHref(),
                             track.getUri());
    }

    private String getFieldsForArtists(Set<SimpleArtist> artists) {
        return artists.stream().map(this::getFieldForArtist).collect(Collectors.joining(", "));
    }

    private String getFieldForArtist(SimpleArtist artist) {
        return String.format("('%s', '%s', '%s', '%s')",
                             artist.getId(),
                             artist.getName(),
                             artist.getHref(),
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
