package chart.postgres;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.wrapper.spotify.models.SimpleArtist;

import chart.ChartSaver;
import chart.spotify.SpotifyChart;

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
        Set<SimpleArtist> artists = chart.entries().stream()
                                         .map(entry -> entry.track().getArtists())
                                         .flatMap(List::stream)
                                         .collect(Collectors.toSet());
        connection.saveArtists(artists);

        connection.saveMetadata(chart);
        connection.saveEntries(chart.week(), chart.entries());
    }
}
