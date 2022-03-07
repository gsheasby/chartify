package chart.tasks;

import chart.ChartConfig;
import chart.ChartRun;
import chart.SimpleChartEntry;
import chart.Song;
import chart.postgres.MultiChartLoader;
import chart.postgres.PostgresConnection;
import chart.postgres.YearEndChartPrinter;
import chart.postgres.YearEndChartSaver;
import chart.postgres.raw.YearEndChartEntryRecord;
import chart.spotify.SimpleSpotifyChartEntry;
import chart.spotify.SpotifyPlaylistLoader;
import com.google.common.collect.Maps;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// TODO - almost total duplicate of YearEndChartPreviewTask
public class YearEndChartSavingTask {
    private static final int LIMIT = 200;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        int year = 2020;
        if (args.length < 1) {
            System.out.println("Using default year of " + year);
        } else {
            year = Integer.parseInt(args[0]);
        }

        ChartConfig config = TaskUtils.getConfig();
        PostgresConnection connection = PostgresConnection.create(config.postgresConfig());

        Map<Song, ChartRun> chartRuns = new MultiChartLoader(config).getAllChartRuns(year);
        List<List<SimpleSpotifyChartEntry>> yecSections = getPartsOfYearEndChart(config);
        Map<Integer, SimpleSpotifyChartEntry> yearEndChart = printYearEndChartPreview(year, connection, chartRuns, yecSections);

        YearEndChartSaver saver = new YearEndChartSaver(connection);
        saver.save(year, yearEndChart);
    }

    private static List<List<SimpleSpotifyChartEntry>> getPartsOfYearEndChart(ChartConfig config) {
        SpotifyPlaylistLoader playlistLoader = SpotifyPlaylistLoader.create(config.spotifyConfig());
        ArrayList<String> playlistIds = config.spotifyConfig().playlists().yecSections();
        return playlistIds.stream().map(playlistLoader::loadChartEntries).collect(Collectors.toList());
    }

    private static Map<Integer, SimpleSpotifyChartEntry> printYearEndChartPreview(
            int year,
            PostgresConnection connection,
            Map<Song, ChartRun> chartRuns,
            List<List<SimpleSpotifyChartEntry>> yecSections) {
        Map<Integer, SimpleSpotifyChartEntry> yearEndChart = Maps.newHashMap();
        Set<YearEndChartEntryRecord> lastYearsTopHundred = connection.getYearEndChartEntries(year - 1, 100);
        System.out.println("Loaded YEC for " + (year - 1) + " from Postgres");

        int pos = 1;
        int sectionIndex = 1;
        for (List<SimpleSpotifyChartEntry> section : yecSections) {
            System.out.println("-- Section " + sectionIndex + " --");
            for (SimpleSpotifyChartEntry entry : section) {
                Song song = entry.toSong();
                ChartRun chartRun = Optional.ofNullable(chartRuns.get(song))
                        .orElseGet(() -> chartRuns.entrySet().stream()
                                .filter(e -> e.getKey().equals(song))
                                .findAny()
                                .map(Map.Entry::getValue)
                                .orElseThrow());
                YearEndChartPrinter.printWithStats(pos, chartRun);
                yearEndChart.put(pos, entry);

                pos++;
            }
            System.out.println();
            sectionIndex++;
        }

        List<ChartRun> remainingEntries = getChartRunsNotInTopSections(chartRuns, yecSections);

        Set<String> trackIdsToExclude = lastYearsTopHundred.stream()
                .map(YearEndChartEntryRecord::track_id)
                .collect(Collectors.toSet());;

        Iterator<ChartRun> yecIterator = remainingEntries.iterator();
        for (int statPos = pos; yecIterator.hasNext() && statPos <= LIMIT; statPos++) {
            ChartRun chartRun = yecIterator.next();

            if (trackIdsToExclude.contains(chartRun.getSong().id())) {
                System.out.println("Skipping " + chartRun.getSong() + " from last year's top 100");
                statPos--;
            } else {
                YearEndChartPrinter.printWithStats(statPos, chartRun);
            }
        }

        return yearEndChart;
    }

    private static List<ChartRun> getChartRunsNotInTopSections(Map<Song, ChartRun> chartRuns, List<List<SimpleSpotifyChartEntry>> topLists) {
        Set<ChartRun> indexedTopRuns = topLists.stream()
                .flatMap(List::stream) // Concatenate lists
                .map(SimpleChartEntry::toSong)
                .map(chartRuns::get)
                .collect(Collectors.toSet());

        return chartRuns.values().stream()
                .sorted()
                .filter(run -> !indexedTopRuns.contains(run))
                .collect(Collectors.toList());
    }
}
