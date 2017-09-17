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
    @Override
    public Chart findDerivedChart(int week) throws IOException {
        throw new NotImplementedException();
    }

    @Override
    public SimpleChart findChart(int week) throws IOException {
        SpotifyPlaylistLoader playlistLoader = new SpotifyPlaylistLoader();
        List<PlaylistTrack> playlist = playlistLoader.load();
        List<Track> tracks = playlist.stream().limit(30).map(PlaylistTrack::getTrack).collect(Collectors.toList());
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
