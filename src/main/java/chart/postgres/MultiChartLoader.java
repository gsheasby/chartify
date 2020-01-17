package chart.postgres;

import chart.Chart;
import chart.ChartConfig;
import chart.ChartEntry;
import chart.ChartRun;
import chart.Song;
import chart.spotify.SpotifyChart;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class MultiChartLoader {
    private final ChartConfig config;

    public MultiChartLoader(ChartConfig config) {
        this.config = config;
    }

    public Map<Song, ChartRun> getAllChartRuns(int year) throws SQLException, ClassNotFoundException {
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        PostgresChartReader reader = new PostgresChartReader(connection);
        PostgresChartLoader loader = new PostgresChartLoader(connection, reader);
        List<SpotifyChart> charts = loader.loadCharts(year);

        Map<Song, ChartRun> chartRuns = Maps.newHashMap();
        for (Chart chart : charts) {
            for (ChartEntry entry : chart.entries()) {
                Song song = entry.toSong();
                if (chartRuns.containsKey(song)) {
                    chartRuns.get(song).add(entry.position());
                } else {
                    ChartRun chartRun = new ChartRun(song, chart.week(), chart.date());
                    chartRun.add(entry.position());
                    chartRuns.put(song, chartRun);
                }
            }
        }

        // Mark which entries were in the last chart
        Iterables.getLast(charts).entries()
                .stream()
                .map(ChartEntry::toSong)
                .forEach(song -> chartRuns.get(song).setActive(true));

        return chartRuns;
    }
}
