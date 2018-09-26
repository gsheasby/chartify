package chart;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.spotify.ChartPosition;
import chart.spotify.SpotifyChartEntry;

public class ChartTestUtils {
    public static final String ARTIST = "Artist";
    public static final String TITLE = "Title";
    public static final String ID = "id";
    public static final String HREF = "href";
    public static final String URI = "uri";

    public static SpotifyChartEntry newEntry() {
        return SpotifyChartEntry.builder()
                                .position(11)
                                .weeksOnChart(1)
                                .track(track())
                                .build();
    }

    static SpotifyChartEntry numberOne() {
        return entry(1, 5);
    }

    static SpotifyChartEntry entry(int position, int lastPosition) {
        return SpotifyChartEntry.builder()
                                .position(position)
                                .lastPosition(lastPosition)
                                .weeksOnChart(2)
                                .track(track())
                                .chartRun(ImmutableList.of(pos(111, lastPosition), pos(112, position)))
                                .build();
    }

    public static SpotifyChartEntry threeWeeks() {
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

    public static Track track() {
        SimpleArtist artist = new SimpleArtist();
        artist.setName("artist");
        Track track = new Track();
        track.setName("title");
        track.setArtists(ImmutableList.of(artist));
        track.setId(ID);
        track.setHref(HREF);
        track.setUri(URI);
        return track;
    }

    private static ChartPosition pos(int week, int position) {
        return ChartPosition.builder().week(week).position(position).build();
    }
}
