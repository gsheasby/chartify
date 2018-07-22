package chart.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Optional;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.Chart;
import chart.ChartReader;
import chart.spotify.ImmutableSimpleSpotifyChart;
import chart.spotify.ImmutableSimpleSpotifyChartEntry;
import chart.spotify.ImmutableSpotifyChart;
import chart.spotify.ImmutableSpotifyChartEntry;
import chart.spotify.SimpleSpotifyChart;
import chart.spotify.SimpleSpotifyChartEntry;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;
import chart.spotify.SpotifyChartReader;

public class PostgresChartCompilerTest {
    @Test
    public void isYoutubeIsPreserved() throws IOException {
        SpotifyChartReader reader = mock(SpotifyChartReader.class);
        ChartReader lastWeekReader = mock(ChartReader.class);

        PostgresChartCompiler compiler = new PostgresChartCompiler(reader, lastWeekReader);

        when(reader.findChart(4)).thenReturn(defaultSpotifyChart(4, true));
        when(lastWeekReader.findDerivedChart(3)).thenReturn(defaultChart(3, true));

        SpotifyChart spotifyChart = compiler.compileChart(4);
        SpotifyChartEntry entry = Iterables.getOnlyElement(spotifyChart.entries());
        assertTrue(entry.isYoutube());
    }

    @Test
    public void dropoutsAreRecorded() throws IOException {
        SpotifyChartReader reader = mock(SpotifyChartReader.class);
        ChartReader lastWeekReader = mock(ChartReader.class);

        PostgresChartCompiler compiler = new PostgresChartCompiler(reader, lastWeekReader);

        when(reader.findChart(2)).thenReturn(defaultSpotifyChart(2, false));
        when(lastWeekReader.findDerivedChart(1)).thenReturn(defaultChart(1, false));

        SpotifyChart spotifyChart = compiler.compileChart(2);

        assertEquals(1, spotifyChart.dropouts().size());
        assertEquals(barEntry(false), spotifyChart.dropouts().get(0));
    }

    private Chart defaultChart(int week, boolean isYoutube) {
        SpotifyChartEntry entry = barEntry(isYoutube);
        return ImmutableSpotifyChart.builder()
                .date(new DateTime(2016, 12, 25, 0, 0))
                .week(week)
                .dropouts(ImmutableList.of())
                .addEntries(entry)
                .build();
    }

    private SpotifyChartEntry barEntry(boolean isYoutube) {
        return ImmutableSpotifyChartEntry.builder()
                                         .weeksOnChart(2)
                                         .position(1)
                                         .lastPosition(Optional.of(1))
                                         .track(getTrack("bar"))
                                         .isYoutube(isYoutube)
                                         .build();
    }

    private SimpleSpotifyChart defaultSpotifyChart(int week, boolean isYoutube) {
        SimpleSpotifyChartEntry entry = ImmutableSimpleSpotifyChartEntry.builder()
                .position(1)
                .track(getTrack("foo"))
                .isYoutube(isYoutube)
                .build();

        return ImmutableSimpleSpotifyChart.builder()
                .week(week)
                .date(new DateTime(2017, 1, 1, 0, 0))
                .addEntries(entry)
                .build();
    }

    private Track getTrack(String title) {
        SimpleArtist artist = new SimpleArtist();
        artist.setName("artist");

        Track track = new Track();
        track.setName(title);
        track.setArtists(ImmutableList.of(artist));
        track.setId("id");
        track.setHref("href");
        track.setUri("uri");
        return track;
    }
}