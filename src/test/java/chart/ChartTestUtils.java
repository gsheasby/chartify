package chart;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.spotify.SpotifyChartEntry;

public class ChartTestUtils {
    static SpotifyChartEntry newEntry() {
        return SpotifyChartEntry.builder()
                                .position(1)
                                .weeksOnChart(1)
                                .track(track())
                                .build();
    }

    static Track track() {
        SimpleArtist artist = new SimpleArtist();
        artist.setName("artist");
        Track track = new Track();
        track.setName("title");
        track.setArtists(ImmutableList.of(artist));
        return track;
    }
}
