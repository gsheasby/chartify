package chart;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import chart.spotify.ChartPosition;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class BbCodeChartFormatterTest {
    private ChartFormatter delegate;
    private BbCodeChartFormatter formatter;

    @Before
    public void setUp() {
        delegate = mock(ChartFormatter.class);
        formatter = new BbCodeChartFormatter(delegate);
    }

    @Test
    public void getsHeaderFromDelegate() {
        SpotifyChart chart = SpotifyChart.builder()
                                         .week(5)
                                         .date(DateTime.now())
                                         .build();

        formatter.getHeader(chart);

        verify(delegate, times(1)).getHeader(chart);
    }

    @Test
    public void getsLineFromDelegate() {
        SpotifyChartEntry entry = ChartTestUtils.newEntry();

        formatter.getLine(entry);

        verify(delegate, times(1)).getLine(entry);
    }

    @Test
    public void getsDropoutTextFromDelegate() {
        SpotifyChartEntry entry = threeWeeks();

        formatter.getDropoutText(entry);

        verify(delegate, times(1)).getDropoutText(entry);
    }

    private SpotifyChartEntry threeWeeks() {
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

    private ChartPosition pos(int week, int position) {
        return ChartPosition.builder().week(week).position(position).build();
    }
}