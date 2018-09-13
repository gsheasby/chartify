package chart;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.spotify.ChartPosition;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PlainTextChartFormatterTest {
    private ChartFormatter formatter;
    private SimpleArtist artist;
    private Track track;

    @Before
    public void setUp() {
        formatter = new PlainTextChartFormatter();

        artist = new SimpleArtist();
        artist.setName("artist");
        track = new Track();
        track.setName("title");
        track.setArtists(ImmutableList.of(artist));
    }

    @Test
    public void headerContainsWeekAndDate() {
        DateTime date = DateTime.parse("2018-06-20");
        SpotifyChart chart = SpotifyChart.builder()
                .week(5)
                .date(date)
                .build();
        String expected = "Week 5: Wednesday, June 20, 2018";

        String header = formatter.getHeader(chart);
        assertEquals(expected, header);
    }

    @Test
    public void newEntryFormat() {
        SpotifyChartEntry entry = SpotifyChartEntry.builder()
                .position(1)
                .weeksOnChart(1)
                .track(track)
                .build();

        String expected = "01 (NE) 1 artist - title";
        String line = formatter.getLine(entry);
        assertEquals(expected, line);
    }

    @Test
    public void lastPositionFormat() {
        SpotifyChartEntry entry = threeWeeks();

        String expected = "05 (07) 3 artist - title [10, 7, 5]";
        String line = formatter.getLine(entry);
        assertEquals(expected, line);
    }

    @Test
    public void dropoutFormat() {
        SpotifyChartEntry entry = threeWeeks();

        String expected = "-- (05) 3 artist - title [10, 7, 5]";
        String line = formatter.getDropoutText(entry);
        assertEquals(expected, line);
    }

    private SpotifyChartEntry threeWeeks() {
        Iterable<ChartPosition> run = ImmutableList.of(
                pos(1, 10), pos(2, 7), pos(3, 5)
        );
        return SpotifyChartEntry.builder()
                                .track(track)
                                .position(5)
                                .lastPosition(7)
                                .weeksOnChart(3)
                                .chartRun(run)
                                .build();
    }

    private ChartPosition pos(int week, int position) {
        return ChartPosition.builder().week(week).position(position).build();
    }

}