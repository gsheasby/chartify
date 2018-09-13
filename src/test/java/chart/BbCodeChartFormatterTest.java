package chart;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.joda.time.DateTime;
import org.junit.Test;

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
        SpotifyChartEntry entry = ChartTestUtils.newEntry();

        formatter.getLine(entry);

        verify(delegate, times(1)).getLine(entry);
    }
}