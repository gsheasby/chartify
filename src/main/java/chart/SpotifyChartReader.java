package chart;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.Track;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class SpotifyChartReader implements ChartReader {
    private final int chartSize;
    private final SpotifyPlaylistLoader playlistLoader;

    public SpotifyChartReader(ChartConfig config) {
        this.chartSize = config.chartSize();
        this.playlistLoader = new SpotifyPlaylistLoader(config.spotifyConfig());
    }

    @Override
    public Chart findDerivedChart(int week) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public SimpleChart findChart(int week) throws IOException {
        List<PlaylistTrack> playlist = playlistLoader.load();
        List<Track> tracks = playlist.stream().limit(chartSize).map(PlaylistTrack::getTrack).collect(Collectors.toList());
        int position = 1;
        List<SimpleChartEntry> entries = Lists.newArrayList();
        for (Track track : tracks) {
            SimpleChartEntry entry = createEntry(position, track);
            position += 1;
            entries.add(entry);
        }

        return ImmutableSimpleChart.builder()
                .week(week)
                .date(DateTime.now())
                .entries(entries)
                .build();
    }

    private SimpleChartEntry createEntry(int position, Track track) {
        String title = track.getName();

        // TODO handle multiple artists
        String artist = track.getArtists().get(0).getName();

        return ImmutableSimpleChartEntry.builder()
                .artist(artist)
                .title(title)
                .position(position)
                .build();
    }
}
