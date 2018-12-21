package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import chart.ChartConfig;
import chart.ChartRun;
import chart.SimpleChartEntry;
import chart.Song;
import chart.postgres.MultiChartLoader;
import chart.postgres.YearEndChartPrinter;
import chart.spotify.SimpleSpotifyChartEntry;
import chart.spotify.SpotifyPlaylistLoader;

public class YearEndChartPreviewTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        int year = 2018;
        if (args.length < 1) {
            System.out.println("Using default year of " + year);
        } else {
            year = Integer.parseInt(args[0]);
        }

        ChartConfig config = TaskUtils.getConfig();

        Map<Song, ChartRun> chartRuns = new MultiChartLoader(config).getAllChartRuns(year);
        List<List<SimpleSpotifyChartEntry>> yecSections = getPartsOfYearEndChart(config);
        printYearEndChartPreview(chartRuns, yecSections);
    }

    private static List<List<SimpleSpotifyChartEntry>> getPartsOfYearEndChart(ChartConfig config) {
        SpotifyPlaylistLoader playlistLoader = SpotifyPlaylistLoader.create(config.spotifyConfig());
        ArrayList<String> playlistIds = config.spotifyConfig().playlists().yecSections();
        return playlistIds.stream().map(playlistLoader::loadChartEntries).collect(Collectors.toList());
    }

    private static void printYearEndChartPreview(Map<Song, ChartRun> chartRuns, List<List<SimpleSpotifyChartEntry>> yecSections) {
        int pos = 1;
        int sectionIndex = 1;
        for (List<SimpleSpotifyChartEntry> section : yecSections) {
            System.out.println("-- Section " + sectionIndex + " --");
            for (SimpleSpotifyChartEntry entry : section) {
                Song song = entry.toSong();
                ChartRun chartRun = chartRuns.get(song);
                YearEndChartPrinter.printSingleSong(pos, chartRun);
                pos++;
            }
            System.out.println();
            sectionIndex++;
        }

        List<ChartRun> remainingEntries = getChartRunsNotInTopSections(chartRuns, yecSections);
        Iterator<ChartRun> yecIterator = remainingEntries.iterator();
        for (int statPos = pos; yecIterator.hasNext(); statPos++) {
            ChartRun chartRun = yecIterator.next();
            YearEndChartPrinter.printSingleSong(statPos, chartRun);
        }
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
