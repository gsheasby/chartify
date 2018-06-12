package chart.postgres;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.ChartSaver;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PostgresChartSaver implements ChartSaver<SpotifyChart> {
    private final PostgresConnection connection;

    public PostgresChartSaver(PostgresConnection connection) {
        this.connection = connection;
    }

    @Override
    public void saveChart(SpotifyChart chart) throws IOException {
        Set<SimpleArtist> artists = chart.entries().stream()
                                         .map(entry -> entry.track().getArtists().get(0))
                                         .collect(Collectors.toSet());
        connection.saveArtists(artists);

        Set<Track> tracks = chart.entries().stream()
                                 .map(SpotifyChartEntry::track)
                                 .collect(Collectors.toSet());
        connection.saveTracks(tracks);
        connection.saveMetadata(chart);
        connection.saveEntries(chart.week(), chart.entries());
    }
}