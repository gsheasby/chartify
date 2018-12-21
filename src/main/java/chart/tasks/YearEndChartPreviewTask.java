package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
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

        List<ChartRun> statisticalYec = chartRuns.values().stream().sorted().collect(Collectors.toList());

        SpotifyPlaylistLoader playlistLoader = SpotifyPlaylistLoader.create(config.spotifyConfig());
        ArrayList<String> playlistIds = config.spotifyConfig().playlists().yecSections();
        List<List<SimpleSpotifyChartEntry>> topLists = playlistIds.stream().map(playlistLoader::loadChartEntries).collect(Collectors.toList());

        List<SimpleSpotifyChartEntry> topEntries = topLists.stream().flatMap(List::stream).collect(Collectors.toList());
        List<Song> topSongs = topEntries.stream().map(SimpleChartEntry::toSong).collect(Collectors.toList());
        List<ChartRun> topRuns = topSongs.stream().map(chartRuns::get).collect(Collectors.toList());

        List<ChartRun> yec = new ArrayList<>(topRuns);
        Set<ChartRun> indexedTopRuns = new HashSet<>(topRuns);

        statisticalYec.stream().filter(run -> !indexedTopRuns.contains(run)).forEach(yec::add);
        YearEndChartPrinter.printYearEndChart(yec);
    }
}
