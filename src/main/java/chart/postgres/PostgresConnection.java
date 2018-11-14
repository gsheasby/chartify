package chart.postgres;

import chart.postgres.raw.ArtistRecord;
import chart.postgres.raw.ChartEntryRecord;
import chart.postgres.raw.ImmutableArtistRecord;
import chart.postgres.raw.ImmutableChartEntryRecord;
import chart.postgres.raw.ImmutableTrackArtistRecord;
import chart.postgres.raw.ImmutableTrackPositionRecord;
import chart.postgres.raw.ImmutableTrackRecord;
import chart.postgres.raw.TrackArtistRecord;
import chart.postgres.raw.TrackPositionRecord;
import chart.postgres.raw.TrackRecord;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import javafx.util.Pair;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PostgresConnection {
    private final PostgresConnectionManager manager;

    public PostgresConnection(PostgresConnectionManager manager) {
        this.manager = manager;
    }

    public static PostgresConnection create(PostgresConfig postgresConfig) throws SQLException, ClassNotFoundException {
        PostgresConnectionManager manager = PostgresConnectionManager.create(postgresConfig);
        return new PostgresConnection(manager);
    }

    // TODO does this abstraction make sense?
    // TODO - what if a new artist has an ID that isn't its spotify ID? Maybe do a lookup for artists when importing from CSV
    // TODO better exception handling
    public void saveArtists(Set<ArtistRecord> artists) {
        try (Connection conn = manager.getConnection()) {
            String sql = getUpdateForArtists(artists);

            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert artists!", e);
        }
    }

    // TODO delegate this so it can be tested
    private String getUpdateForArtists(Set<ArtistRecord> artists) {
        return "INSERT INTO artists (id, name, href, uri, is_youtube)" +
                        " VALUES " + getFieldsForArtists(artists) +
                        " ON CONFLICT DO NOTHING";
    }

    public void saveMetadata(SpotifyChart chart) {
        try (Connection conn = manager.getConnection()) {
            Statement statement = conn.createStatement();
            DateTime chartDate = chart.date();
            String sql = String.format("INSERT INTO chart (week, date) VALUES (%d, '%s-%s-%s')",
                                       chart.week(),
                                       chartDate.year().get(),
                                       chartDate.monthOfYear().get(),
                                       chartDate.dayOfMonth().get());

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save chart!", e);
        }
    }

    public void saveEntries(int week, List<SpotifyChartEntry> entries) {
        try (Connection conn = manager.getConnection()) {
            Statement statement = conn.createStatement();

            // Save the tracks
            String insertTracks = "INSERT INTO tracks (id, name, href, uri, is_youtube)" +
                    " VALUES " + getTrackFieldsForEntries(entries) +
                    " ON CONFLICT DO NOTHING";

            statement.executeUpdate(insertTracks);

            // Connect tracks to artists
            Set<Track> tracks = entries.stream().map(SpotifyChartEntry::track).collect(Collectors.toSet());
            String insertTrackArtists = "INSERT INTO trackArtists (track_id, artist_id)" +
                    " VALUES " + getArtistsForTracks(tracks) +
                    " ON CONFLICT DO NOTHING";
            statement.executeUpdate(insertTrackArtists);

            // Insert entries
            String sql = "INSERT INTO chartEntries (chart_week, position, track_id)" +
                    " VALUES " + getFieldsForEntries(week, entries) +
                    " ON CONFLICT DO NOTHING";

            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert tracks!", e);
        }
    }

    private String getTrackFieldsForEntries(List<SpotifyChartEntry> entries) {
        return entries.stream().map(this::getTrackFieldsForEntry).collect(Collectors.joining(", "));
    }

    private String getTrackFieldsForEntry(SpotifyChartEntry entry) {
        Track track = entry.track();
        return String.format("('%s', '%s', '%s', '%s', %s)",
                             track.getId(),
                             escapeQuotes(track.getName()),
                             track.getHref(),
                             track.getUri(),
                             entry.isYoutube());
    }

    private static String escapeQuotes(String name) {
        return StringUtils.replace(name, "'", "''");
    }

    private String getFieldsForEntries(int week, List<SpotifyChartEntry> entries) {
        return entries.stream().map(entry -> getFieldsForEntry(week, entry)).collect(Collectors.joining(", "));
    }

    private String getFieldsForEntry(int week, SpotifyChartEntry entry) {
        return String.format("(%d, %d, '%s')", week, entry.position(), entry.track().getId());
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
        return String.format("('%s', '%s')", track.getId(), artist.getId());
    }

    private String getFieldsForArtists(Set<ArtistRecord> artists) {
        return artists.stream().map(this::getFieldForArtist).collect(Collectors.joining(", "));
    }

    private String getFieldForArtist(ArtistRecord artist) {
        return String.format("('%s', '%s', '%s', '%s', %s)",
                             artist.id(),
                             escapeQuotes(artist.name()),
                             artist.href(),
                             artist.uri(),
                             artist.is_youtube());
    }

    public Optional<Integer> getPosition(String trackId, int week) {
        String sql = "SELECT e.position AS lastPos" +
                "     FROM chartEntries e" +
                "     WHERE e.track_id = " + trackId +
                "     AND e.chart_week = " + week;
        Function<ResultSet, Optional<Integer>> mapper = resultSet -> {
            try {
                return Optional.of(resultSet.getInt("lastPos"));
            } catch (SQLException e) {
                throw new RuntimeException("Couldn't get last position!");
            }
        };
        return executeSelectSingleStatement(sql, mapper, Optional.empty());
    }

    public Map<String, Integer> getPositions(Set<String> trackIds, int week) {
        String sql = "SELECT e.track_id AS id, e.position AS lastPos" +
                "     FROM chartEntries e" +
                "     WHERE e.chart_week = " + week +
                "     AND e.track_id IN " + getInClause(trackIds);
        Function<ResultSet, Pair<String, Integer>> mapper = resultSet -> {
            try {
                String id = resultSet.getString("id");
                Integer lastPos = resultSet.getInt("lastPos");
                return new Pair<>(id, lastPos);
            } catch (SQLException e) {
                throw new RuntimeException("Couldn't extract chart entry!");
            }
        };

        List<Pair<String, Integer>> entries = executeSelectStatement(sql, mapper);
        return entries.stream().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public Map<String, Integer> getWeeksOnChart(Set<String> trackIds, int upToWeek) {
        String sql = "SELECT e.track_id AS id, COUNT(e.track_id) AS weeks" +
                "     FROM chartEntries e" +
                "     WHERE e.track_id IN " + getInClause(trackIds) +
                "     AND e.chart_week <= " + upToWeek +
                "     GROUP BY e.track_id";
        Function<ResultSet, Pair<String, Integer>> mapper = resultSet -> {
            try {
                String id = resultSet.getString("id");
                Integer weeks = resultSet.getInt("weeks");
                return new Pair<>(id, weeks);
            } catch (SQLException e) {
                throw new RuntimeException("Couldn't extract chart entry!");
            }
        };

        List<Pair<String, Integer>> entries = executeSelectStatement(sql, mapper);
        return entries.stream().collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    public List<TrackPositionRecord> getTrackPositions(int week) {
        // TODO possibly a WITH query?
        String sql =  "SELECT e.position AS pos, t.id AS id, t.name AS name," +
                " t.href AS href, t.uri AS uri, t.is_youtube AS is_youtube" +
                "      FROM tracks t" +
                "      JOIN chartEntries e ON t.id = e.track_id" +
                "      WHERE e.chart_week = " + week;
        return executeSelectStatement(sql, this::createChartEntryRecord);
    }

    public List<ChartEntryRecord> getChartEntries(Set<String> trackIds, int upToWeek) {
        String sql = "SELECT chart_week, position, track_id" +
                "     FROM chartEntries e" +
                "     WHERE track_id IN " + getInClause(trackIds) +
                "     AND chart_week <= " + upToWeek;
        Function<ResultSet, ChartEntryRecord> mapper = resultSet -> {
            try {
                return ImmutableChartEntryRecord.builder()
                        .chart_week(resultSet.getInt("chart_week"))
                        .position(resultSet.getInt("position"))
                        .track_id(resultSet.getString("track_id"))
                        .build();
            } catch (SQLException e) {
                throw new RuntimeException("Couldn't extract chart entry!", e);
            }
        };
        return executeSelectStatement(sql, mapper);
    }

    private TrackPositionRecord createChartEntryRecord(ResultSet resultSet) {
        try {
            return ImmutableTrackPositionRecord.builder()
                                               .position(resultSet.getInt("pos"))
                                               .track_id(resultSet.getString("id"))
                                               .track_name(resultSet.getString("name"))
                                               .track_href(resultSet.getString("href"))
                                               .track_uri(resultSet.getString("uri"))
                                               .is_youtube(resultSet.getBoolean("is_youtube"))
                                               .build();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create chart entry record!", e);
        }
    }

    public List<TrackArtistRecord> getTrackArtists(Set<String> trackIds) {
        String sql = "SELECT track_id, artist_id FROM trackArtists" +
                "    WHERE track_id IN " + getInClause(trackIds);

        return executeSelectStatement(sql, this::createTrackArtistRecord);
    }

    private TrackArtistRecord createTrackArtistRecord(ResultSet resultSet) {
        try {
            return ImmutableTrackArtistRecord.builder()
                                             .track_id(resultSet.getString("track_id"))
                                             .artist_id(resultSet.getString("artist_id"))
                                             .build();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create a trackArtistRecord!", e);
        }
    }

    public Map<String, ArtistRecord> getArtists(Set<String> artistIds) {
        String sql = "SELECT id, name, href, uri, is_youtube FROM artists" +
                "    WHERE id IN " + getInClause(artistIds);

        List<ArtistRecord> entries = executeSelectStatement(sql, this::createSimpleArtist);
        return entries.stream().collect(Collectors.toMap(ArtistRecord::id, artist -> artist));
    }

    public Optional<ArtistRecord> getArtist(String artistName) {
        String sql = "SELECT id, name, href, uri, is_youtube FROM artists" +
                "    WHERE name = " + quote(artistName);

        List<ArtistRecord> artists = executeSelectStatement(sql, this::createSimpleArtist);
        return artists.isEmpty() ? Optional.empty() : Optional.of(Iterables.getOnlyElement(artists));
    }

    public Optional<TrackRecord> getTrack(String title, String artistId) {
        String sql = "SELECT t.id, t.name, t.href, t.uri, t.is_youtube FROM tracks t" +
                "     JOIN trackArtists ta ON t.id = ta.track_id" +
                "     WHERE ta.artist_id = " + quote(artistId) +
                "     AND t.name = " + quote(title);

        List<TrackRecord> tracks = executeSelectStatement(sql, this::createTrackRecord);
        return tracks.isEmpty() ? Optional.empty() : Optional.of(Iterables.getOnlyElement(tracks));
    }

    private static String quote(String term) {
        return "'" + escapeQuotes(term) + "'";
    }

    private TrackRecord createTrackRecord(ResultSet resultSet) {
        try {
            return ImmutableTrackRecord.builder()
                    .id(resultSet.getString("id"))
                    .name(resultSet.getString("name"))
                    .href(resultSet.getString("href"))
                    .uri(resultSet.getString("uri"))
                    .is_youtube(resultSet.getBoolean("is_youtube"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create track!", e);
        }
    }

    private ArtistRecord createSimpleArtist(ResultSet resultSet) {
        try {
            return ImmutableArtistRecord.builder()
                    .id(resultSet.getString("id"))
                    .name(resultSet.getString("name"))
                    .href(resultSet.getString("href"))
                    .uri(resultSet.getString("uri"))
                    .is_youtube(resultSet.getBoolean("is_youtube"))
                    .build();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create artist!", e);
        }
    }

    private <T> List<T> executeSelectStatement(String sql, Function<ResultSet, T> mapper) {
        try (Connection conn = manager.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            List<T> results = Lists.newArrayList();
            while (resultSet.next()) {
                T record = mapper.apply(resultSet);
                results.add(record);
            }

            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute select statement: " + sql, e);
        }
    }

    private String getInClause(Set<String> ids) {
        return String.format("(%s)", ids.stream()
                                        .map(id -> String.format("'%s'", id))
                                        .collect(Collectors.joining(", ")));
    }

    public DateTime getChartDate(int week) {
        String sql = "SELECT date FROM chart WHERE week = " + week; // TODO use "where week = ?"
        return executeSelectSingleStatement(sql, this::getDateTime, DateTime.now());
    }

    public int getLatestWeek() {
        String sql = "SELECT max(week) AS latest FROM chart";
        Function<ResultSet, Integer> mapper = resultSet -> {
            try {
                return resultSet.getInt("latest");
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get latest week!");
            }
        };
        return executeSelectSingleStatement(sql, mapper, 0);
    }

    private <T> T executeSelectSingleStatement(String sql, Function<ResultSet, T> mapper, T defaultResult) {
        try (Connection conn = manager.getConnection()) {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return mapper.apply(resultSet);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute select statement!", e);
        }
        return defaultResult;
    }

    private DateTime getDateTime(ResultSet resultSet) {
        try {
            Date date = resultSet.getDate("date");
            return DateTime.parse(date.toString());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get chart date!");
        }
    }
}
