package chart.postgres;

import java.util.List;
import java.util.stream.Collectors;

import chart.spotify.SpotifyChart;

public class PostgresChartLoader {
    private final PostgresConnection connection;
    private final PostgresChartReader reader;

    public PostgresChartLoader(PostgresConnection connection, PostgresChartReader reader) {
        this.connection = connection;
        this.reader = reader;
    }

    public List<SpotifyChart> loadCharts(int year) {
        List<Integer> weeks = connection.getChartWeeks(year);
        return weeks.stream()
                    .map(reader::findDerivedChart)
                    .collect(Collectors.toList());
    }
}
