package chart.postgres;

import chart.spotify.SimpleSpotifyChartEntry;

import java.util.Map;

public class YearEndChartSaver {
    private final PostgresConnection connection;

    public YearEndChartSaver(PostgresConnection connection) {
        this.connection = connection;
    }

    public void save(int year, Map<Integer, SimpleSpotifyChartEntry> yearEndChart) {
        // TODO
    }
}
