package chart.tasks;

import chart.ChartConfig;
import chart.ChartRun;
import chart.SimpleChartEntry;
import chart.Song;
import chart.postgres.MultiChartLoader;
import chart.postgres.PostgresConnection;
import chart.postgres.YearEndChartPrinter;
import chart.postgres.raw.YearEndChartEntryRecord;
import chart.spotify.SimpleSpotifyChartEntry;
import chart.spotify.SpotifyPlaylistLoader;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class YearEndChartPreviewTask {
    private static final int LIMIT = 200;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        int year = 2019;
        if (args.length < 1) {
            System.out.println("Using default year of " + year);
        } else {
            year = Integer.parseInt(args[0]);
        }

        ChartConfig config = TaskUtils.getConfig();
        PostgresConnection connection = PostgresConnection.create(config.postgresConfig());

        Map<Song, ChartRun> chartRuns = new MultiChartLoader(config).getAllChartRuns(year);
        System.out.println("Loaded charts for " + year);
        List<List<SimpleSpotifyChartEntry>> yecSections = getPartsOfYearEndChart(config);
        System.out.println("Loaded year-end charts from Spotify");
        printYearEndChartPreview(year, connection, chartRuns, yecSections);
    }

    private static List<List<SimpleSpotifyChartEntry>> getPartsOfYearEndChart(ChartConfig config) {
        SpotifyPlaylistLoader playlistLoader = SpotifyPlaylistLoader.create(config.spotifyConfig());
        ArrayList<String> playlistIds = config.spotifyConfig().playlists().yecSections();
        return playlistIds.stream().map(playlistLoader::loadChartEntries).collect(Collectors.toList());
    }

    private static void printYearEndChartPreview(
            int year,
            PostgresConnection connection,
            Map<Song, ChartRun> chartRuns,
            List<List<SimpleSpotifyChartEntry>> yecSections) {
        Set<YearEndChartEntryRecord> lastYearsTopHundred = connection.getYearEndChartEntries(year - 1, 100);
        System.out.println("Loaded YEC for " + (year - 1) + " from Postgres");

        List<String> entriesForPrinting = Lists.newArrayListWithCapacity(LIMIT);

        int pos = 1;
        int sectionIndex = 1;
        for (List<SimpleSpotifyChartEntry> section : yecSections) {
            System.out.println("-- Section " + sectionIndex + " --");
            for (SimpleSpotifyChartEntry entry : section) {
                Song song = entry.toSong();
                ChartRun chartRun = chartRuns.get(song);
                YearEndChartPrinter.printWithStats(pos, chartRun);
                entriesForPrinting.add(YearEndChartPrinter.getBbCodedString(pos, chartRun));
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
                entriesForPrinting.add("Skipping " + chartRun.getSong() + " from last year's top 100");
                statPos--; // TODO this is rather hacky!
                continue;
            }

            entriesForPrinting.add(YearEndChartPrinter.getBbCodedString(statPos, chartRun));
        }

        Lists.reverse(entriesForPrinting).forEach(System.out::println);
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
