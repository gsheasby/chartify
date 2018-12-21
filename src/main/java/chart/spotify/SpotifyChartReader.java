package chart.spotify;

import java.util.List;
import java.util.function.Function;

import org.joda.time.DateTime;

import chart.ChartConfig;
import chart.SimpleChartReader;

public class SpotifyChartReader implements SimpleChartReader<SimpleSpotifyChart> {
    private final int chartSize;
    private final SpotifyConfig spotifyConfig;
    private final SpotifyPlaylistLoader playlistLoader;
    private final Function<SpotifyConfig, String> playlistToLoad;

    private SpotifyChartReader(ChartConfig config, Function<SpotifyConfig, String> playlistToLoad) {
        this.chartSize = config.chartSize();
        this.spotifyConfig = config.spotifyConfig();
        this.playlistLoader = SpotifyPlaylistLoader.create(config.spotifyConfig());
        this.playlistToLoad = playlistToLoad;
    }

    public static SpotifyChartReader chartReader(ChartConfig config) {
        return new SpotifyChartReader(config, spotifyConfig -> spotifyConfig.playlists().chart());
    }

    public static SpotifyChartReader yecReader(ChartConfig config) {
        return new SpotifyChartReader(config, spotifyConfig -> spotifyConfig.playlists().yec());
    }

    @Override
    public SimpleSpotifyChart findChart(int week) {
        List<SimpleSpotifyChartEntry> entries = playlistLoader.loadChartEntries(playlistToLoad.apply(spotifyConfig), chartSize);

        return ImmutableSimpleSpotifyChart.builder()
                                          .week(week)
                                          .date(DateTime.now())
                                          .entries(entries)
                                          .build();
    }
}
