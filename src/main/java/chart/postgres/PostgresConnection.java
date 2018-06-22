package chart.postgres;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.postgres.raw.ChartEntryRecord;
import chart.postgres.raw.TrackArtistRecord;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PostgresConnection {
    private final PostgresConnectionManager manager;

    public PostgresConnection(PostgresConnectionManager manager) {
        this.manager = manager;
    }

    // TODO does this abstraction make sense?
    // TODO - what if a new artist has an ID that isn't its spotify ID? Maybe do a lookup for artists when importing from CSV
    // TODO better exception handling
    public void saveArtists(Set<SimpleArtist> artists) {
        try (Connection conn = manager.getConnection()) {
            String sql = getUpdateForArtists(artists);
            System.out.println(sql);

            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert artists!", e);
        }
    }

    // TODO delegate this so it can be tested
    private String getUpdateForArtists(Set<SimpleArtist> artists) {
        return "INSERT INTO artists (id, name, href, uri) " +
                        "VALUES " + getFieldsForArtists(artists) +
                        " ON CONFLICT DO NOTHING";
    }

    public void saveTracks(Set<Track> tracks) {
        try (Connection conn = manager.getConnection()) {
            // Save the tracks
            String sql = "INSERT INTO tracks (id, name, href, uri) " +
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
            throw new RuntimeException("Failed to insert tracks!", e);
        }
    }

    public void saveEntries(int week, List<SpotifyChartEntry> entries) {
        try (Connection conn = manager.getConnection()) {
            String sql = "INSERT INTO chartEntries (chart_week, position, track_id) " +
                    "VALUES " + getFieldsForEntries(week, entries) +
                    " ON CONFLICT DO NOTHING";

            Statement statement = conn.createStatement();
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert tracks!", e);
        }
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

    private String getFieldsForTracks(Set<Track> tracks) {
        return tracks.stream().map(this::getFieldsForTrack).collect(Collectors.joining(", "));
    }

    private String getFieldsForTrack(Track track) {
        return String.format("('%s', '%s', '%s', '%s')",
                             track.getId(),
                             StringUtils.replace(track.getName(), "'", "''"),
                             track.getHref(),
                             track.getUri());
    }

    private String getFieldsForArtists(Set<SimpleArtist> artists) {
        return artists.stream().map(this::getFieldForArtist).collect(Collectors.joining(", "));
    }

    private String getFieldForArtist(SimpleArtist artist) {
        return String.format("('%s', '%s', '%s', '%s')",
                             artist.getId(),
                             StringUtils.replace(artist.getName(), "'", "''"),
                             artist.getHref(),
                             artist.getUri());
    }

    public List<ChartEntryRecord> getChartEntries(int week) {
        // TODO
        return ImmutableList.of();
    }

    public List<TrackArtistRecord> getTrackArtists(Set<String> trackIds) {
        // TODO
        return ImmutableList.of();
    }

    public Map<String, SimpleArtist> getArtists(Set<String> artistIds) {
        // TODO
        return ImmutableMap.of();
    }

    public DateTime getChartDate(int week) {
        // TODO
        return DateTime.now();
    }
}
