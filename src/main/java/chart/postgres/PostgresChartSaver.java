package chart.postgres;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import chart.ChartSaver;
import chart.postgres.raw.ArtistRecord;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PostgresChartSaver implements ChartSaver<SpotifyChart> {
    private final PostgresConnection connection;

    public PostgresChartSaver(PostgresConnection connection) {
        this.connection = connection;
    }

    public static PostgresChartSaver create(PostgresConfig postgresConfig) throws SQLException, ClassNotFoundException {
        PostgresConnection connection = PostgresConnection.create(postgresConfig);
        return new PostgresChartSaver(connection);
    }

    @Override
    public void saveChart(SpotifyChart chart) {
        Set<ArtistRecord> artists = chart.entries().stream()
                                         .map(this::getArtistRecords)
                                         .flatMap(List::stream)
                                         .collect(Collectors.toSet());
        connection.saveArtists(artists);

        connection.saveMetadata(chart);
        connection.saveEntries(chart.week(), chart.entries());
    }

    private List<ArtistRecord> getArtistRecords(SpotifyChartEntry entry) {
        return entry.track()
                    .getArtists()
                    .stream()
                    .map(artist -> ArtistRecord.from(artist, entry.isYoutube()))
                    .collect(Collectors.toList());
    }
}
