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
        // TODO richer Chart
        Set<SimpleArtist> artists = chart.entries().stream()
                                         .map(entry -> entry.track().getArtists().get(0))
                                         .collect(Collectors.toSet());

        // TODO save artists to Artists table, if necessary
        connection.saveArtists(artists);

        // TODO Then save new tracks to Tracks table
        Set<Track> tracks = chart.entries().stream()
                                 .map(SpotifyChartEntry::track)
                                 .collect(Collectors.toSet());
        connection.saveTracks(tracks);

        // TODO Then save chart entries and chart metadata
//        chart.entries().stream().map(entry -> entry.artist());
    }
}
