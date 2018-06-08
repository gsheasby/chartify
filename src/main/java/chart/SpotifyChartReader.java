package chart;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.Track;

import chart.spotify.ImmutableSimpleSpotifyChart;
import chart.spotify.ImmutableSimpleSpotifyChartEntry;
import chart.spotify.SimpleSpotifyChart;
import chart.spotify.SimpleSpotifyChartEntry;

public class SpotifyChartReader implements SimpleChartReader<SimpleSpotifyChart> {
    private final int chartSize;
    private final SpotifyPlaylistLoader playlistLoader;

    public SpotifyChartReader(ChartConfig config) {
        this.chartSize = config.chartSize();
        this.playlistLoader = new SpotifyPlaylistLoader(config.spotifyConfig());
    }

    @Override
    public SimpleSpotifyChart findChart(int week) {
        List<PlaylistTrack> playlist = playlistLoader.load();
        List<Track> tracks = playlist.stream().limit(chartSize).map(PlaylistTrack::getTrack).collect(Collectors.toList());
        int position = 1;
        List<SimpleSpotifyChartEntry> entries = Lists.newArrayList();
        for (Track track : tracks) {
            SimpleSpotifyChartEntry entry = createEntry(position, track);
            position += 1;
            entries.add(entry);
        }

        return ImmutableSimpleSpotifyChart.builder()
                                          .week(week)
                                          .date(DateTime.now())
                                          .entries(entries)
                                          .build();
    }

    private SimpleSpotifyChartEntry createEntry(int position, Track track) {
        return ImmutableSimpleSpotifyChartEntry.builder()
                                               .position(position)
                                               .track(track)
                                               .build();
    }
}
