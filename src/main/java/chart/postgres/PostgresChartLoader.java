package chart.postgres;

import chart.spotify.SpotifyChart;
import java.util.List;
import java.util.stream.Collectors;

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
                    .sorted(Integer::compareTo)
                    .map(reader::findDerivedChart)
                    .collect(Collectors.toList());
    }
}
