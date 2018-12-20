package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Maps;

import chart.Chart;
import chart.ChartConfig;
import chart.ChartEntry;
import chart.ChartRun;
import chart.SimpleChartEntry;
import chart.Song;
import chart.postgres.PostgresChartLoader;
import chart.postgres.PostgresChartReader;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SimpleSpotifyChart;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartReader;

// TODO duplicate of ChartOfChartsTask
public class YearEndChartPreviewTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        int year = 2018;
        if (args.length < 1) {
            System.out.println("Using default year of " + year);
        } else {
            year = Integer.parseInt(args[0]);
        }

        ChartConfig config = TaskUtils.getConfig();
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        PostgresChartReader reader = new PostgresChartReader(connection);
        PostgresChartLoader loader = new PostgresChartLoader(connection, reader);
        SpotifyChartReader spotifyChartReader = SpotifyChartReader.yecReader(config);

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

        List<ChartRun> statisticalYec = chartRuns.values().stream().sorted().collect(Collectors.toList());

        SimpleSpotifyChart chart = spotifyChartReader.findChart(year);
        List<Song> topSongs = chart.entries().stream().map(SimpleChartEntry::toSong).collect(Collectors.toList());
        List<ChartRun> topRuns = topSongs.stream().map(chartRuns::get).collect(Collectors.toList());

        List<ChartRun> yec = new ArrayList<>(topRuns);
        Set<ChartRun> indexedTopRuns = new HashSet<>(topRuns);

        statisticalYec.stream().filter(run -> !indexedTopRuns.contains(run)).forEach(yec::add);
        Iterator<ChartRun> yecIterator = yec.iterator();

        for (int pos = 1; yecIterator.hasNext(); pos++) {
            ChartRun chartRun = yecIterator.next();
            print(pos, chartRun);
        }
    }

    private static void print(int pos, ChartRun chartRun) {
        System.out.println(String.format("%02d\t%s", pos, chartRun.toString()));
    }
}
