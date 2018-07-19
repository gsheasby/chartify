package chart.spotify;

import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleArtist;
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
        if (spotifyConfig.mappings().containsKey(track.getId())) {
            Track mappedTrack = getMappedTrack(spotifyConfig.mappings().get(track.getId()));
            return ImmutableSimpleSpotifyChartEntry.builder()
                                                   .position(position)
                                                   .track(mappedTrack)
                                                   .build();
        } else {
            return ImmutableSimpleSpotifyChartEntry.builder()
                                                   .position(position)
                                                   .track(track)
                                                   .build();
        }
    }

    private Track getMappedTrack(YoutubeMapping youtubeMapping) {
        Track mappedTrack = new Track();
        mappedTrack.setId(youtubeMapping.id());
        mappedTrack.setName(youtubeMapping.title());
        String href = "http://www.youtube.com/watch?v=" + youtubeMapping.id();
        mappedTrack.setHref(href);
        mappedTrack.setUri(href);

        SimpleArtist artist = new SimpleArtist();
        artist.setName(youtubeMapping.artist());
        artist.setId(youtubeMapping.id());
        mappedTrack.setArtists(ImmutableList.of(artist));

        return mappedTrack;
    }
}
