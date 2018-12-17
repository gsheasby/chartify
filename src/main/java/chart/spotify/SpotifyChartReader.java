package chart.spotify;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.Track;

import chart.ChartConfig;
import chart.SimpleChartReader;

public class SpotifyChartReader implements SimpleChartReader<SimpleSpotifyChart> {
    private final int chartSize;
    private final SpotifyConfig spotifyConfig;
    private final SpotifyPlaylistLoader playlistLoader;

    public SpotifyChartReader(ChartConfig config) {
        this.chartSize = config.chartSize();
        this.spotifyConfig = config.spotifyConfig();
        this.playlistLoader = SpotifyPlaylistLoader.create(config.spotifyConfig());
    }

    @Override
    public SimpleSpotifyChart findChart(int week) {
        List<PlaylistTrack> playlist = playlistLoader.load(spotifyConfig.playlists().chart());
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
        if (spotifyConfig.mappings().containsKey(track.getId())) {
            Track mappedTrack = spotifyConfig.mappings().get(track.getId()).getMappedTrack();
            return ImmutableSimpleSpotifyChartEntry.builder()
                                                   .position(position)
                                                   .track(mappedTrack)
                                                   .isYoutube(true)
                                                   .build();
        } else {
            return ImmutableSimpleSpotifyChartEntry.builder()
                                                   .position(position)
                                                   .track(track)
                                                   .isYoutube(false)
                                                   .build();
        }
    }
}
