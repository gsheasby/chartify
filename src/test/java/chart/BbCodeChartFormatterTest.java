package chart;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class BbCodeChartFormatterTest {
    @Test
    public void getsHeaderFromDelegate() {
        ChartFormatter delegate = mock(ChartFormatter.class);
        BbCodeChartFormatter formatter = new BbCodeChartFormatter(delegate);
        SpotifyChart chart = SpotifyChart.builder()
                                         .week(5)
                                         .date(DateTime.now())
                                         .build();

        formatter.getHeader(chart);

        verify(delegate, times(1)).getHeader(chart);
    }

    @Test
    public void getsLineFromDelegate() {
        ChartFormatter delegate = mock(ChartFormatter.class);
        BbCodeChartFormatter formatter = new BbCodeChartFormatter(delegate);
        SpotifyChartEntry entry = getEntry();

        formatter.getLine(entry);

        verify(delegate, times(1)).getLine(entry);
    }

    // TODO copied from PlainTextCFT
    private SpotifyChartEntry getEntry() {
        return SpotifyChartEntry.builder()
                                .position(1)
                                .weeksOnChart(1)
                                .track(getTrack())
                                .build();
    }

    private Track getTrack() {
        SimpleArtist artist = new SimpleArtist();
        artist.setName("artist");
        Track track = new Track();
        track.setName("title");
        track.setArtists(ImmutableList.of(artist));
        return track;
    }

}