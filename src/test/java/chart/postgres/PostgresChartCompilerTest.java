package chart.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.spotify.ChartPosition;
import chart.spotify.ImmutableChartPosition;
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
    public void isYoutubeIsPreserved() {
        SpotifyChartReader reader = mock(SpotifyChartReader.class);
        PostgresChartReader lastWeekReader = mock(PostgresChartReader.class);

        PostgresChartCompiler compiler = new PostgresChartCompiler(reader, lastWeekReader);

        when(reader.findChart(4)).thenReturn(defaultSpotifyChart(4, true));
        when(lastWeekReader.findDerivedChart(3)).thenReturn(defaultChart(3, true));

        SpotifyChart spotifyChart = compiler.compileChart(4);
        SpotifyChartEntry entry = Iterables.getOnlyElement(spotifyChart.entries());
        assertTrue(entry.isYoutube());
    }

    @Test
    public void chartRunsAreCopiedAcross() {
        SpotifyChartReader reader = mock(SpotifyChartReader.class);
        PostgresChartReader lastWeekReader = mock(PostgresChartReader.class);

        PostgresChartCompiler compiler = new PostgresChartCompiler(reader, lastWeekReader);
        SpotifyChart lastWeek = defaultChart(3, false);
        when(lastWeekReader.findDerivedChart(3)).thenReturn(lastWeek);

        SpotifyChartEntry lastBar = Iterables.getOnlyElement(lastWeek.entries());
        Set<ChartPosition> chartRun = Sets.newHashSet(lastBar.chartRun());
        chartRun.add(getChartPosition(4, 2));

        // SimpleEntry
        SimpleSpotifyChartEntry simpleBazEntry = ImmutableSimpleSpotifyChartEntry.builder()
                                                                                 .track(getTrack("baz"))
                                                                                 .isYoutube(false)
                                                                                 .position(1)
                                                                                 .build();
        SimpleSpotifyChartEntry simpleBarEntry = ImmutableSimpleSpotifyChartEntry.builder()
                .track(lastBar.track())
                .position(2)
                .isYoutube(lastBar.isYoutube())
                .build();
        SimpleSpotifyChart newChart = ImmutableSimpleSpotifyChart.builder()
                .date(lastWeek.date().plusDays(7))
                .week(4)
                .addEntries(simpleBazEntry, simpleBarEntry)
                .build();
        when(reader.findChart(4)).thenReturn(newChart);


        // Expected
        SpotifyChartEntry bazEntry = ImmutableSpotifyChartEntry.builder()
                .track(simpleBazEntry.track())
                .isYoutube(simpleBazEntry.isYoutube())
                .position(1)
                .weeksOnChart(1)
                .addChartRun(getChartPosition(4, 1))
                .build();
        SpotifyChartEntry barEntry = ImmutableSpotifyChartEntry.builder()
                .track(lastBar.track())
                .isYoutube(lastBar.isYoutube())
                .weeksOnChart(chartRun.size())
                .lastPosition(1)
                .position(2)
                .chartRun(chartRun)
                .build();
        SpotifyChart expected = ImmutableSpotifyChart.builder()
                .week(4)
                .date(newChart.date())
                .addEntries(bazEntry, barEntry)
                .dropouts(ImmutableList.of())
                .build();

        SpotifyChart actual = compiler.compileChart(4);

        assertEquals(expected, actual);
        for (int i=0; i < expected.entries().size(); i++) {
            Set<ChartPosition> expectedRun = expected.entries().get(0).chartRun();
            Set<ChartPosition> actualRun = actual.entries().get(0).chartRun();
            assertEquals(expectedRun, actualRun);
        }

    }

    @Test
    public void dropoutsAreRecorded() {
        SpotifyChartReader reader = mock(SpotifyChartReader.class);
        PostgresChartReader lastWeekReader = mock(PostgresChartReader.class);

        PostgresChartCompiler compiler = new PostgresChartCompiler(reader, lastWeekReader);

        when(reader.findChart(2)).thenReturn(defaultSpotifyChart(2, false));
        when(lastWeekReader.findDerivedChart(1)).thenReturn(defaultChart(1, false));

        SpotifyChart spotifyChart = compiler.compileChart(2);

        assertEquals(1, spotifyChart.dropouts().size());
        assertEquals(barEntry(1, false), spotifyChart.dropouts().get(0));
    }

    private SpotifyChart defaultChart(int week, boolean isYoutube) {
        SpotifyChartEntry entry = barEntry(week, isYoutube);
        return ImmutableSpotifyChart.builder()
                .date(new DateTime(2016, 12, 25, 0, 0))
                .week(week)
                .dropouts(ImmutableList.of())
                .addEntries(entry)
                .build();
    }

    private SpotifyChartEntry barEntry(int week, boolean isYoutube) {
        int position = 1;
        ChartPosition currentWeek = getChartPosition(week, position);
        ChartPosition lastWeek = getChartPosition(week - 1, position);

        return ImmutableSpotifyChartEntry.builder()
                                         .weeksOnChart(2)
                                         .position(position)
                                         .lastPosition(Optional.of(position))
                                         .track(getTrack("bar"))
                                         .isYoutube(isYoutube)
                                         .addChartRun(currentWeek, lastWeek)
                                         .build();
    }

    private ChartPosition getChartPosition(int week, int position) {
        return ImmutableChartPosition.builder()
                                     .week(week)
                                     .position(position)
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