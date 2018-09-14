package chart;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.spotify.ChartPosition;
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

    static SpotifyChartEntry threeWeeks() {
        Iterable<ChartPosition> run = ImmutableList.of(
                pos(1, 10), pos(2, 7), pos(3, 5)
        );
        return SpotifyChartEntry.builder()
                                .track(ChartTestUtils.track())
                                .position(5)
                                .lastPosition(7)
                                .weeksOnChart(3)
                                .chartRun(run)
                                .build();
    }

    private static ChartPosition pos(int week, int position) {
        return ChartPosition.builder().week(week).position(position).build();
    }
}
