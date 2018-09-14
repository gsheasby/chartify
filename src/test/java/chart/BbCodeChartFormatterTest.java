package chart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

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
        SpotifyChartEntry entry = ChartTestUtils.threeWeeks();

        formatter.getDropoutText(entry);

        verify(delegate, times(1)).getDropoutText(entry);
    }

    @Test
    public void numberOneShouldBeBold() {
        SpotifyChartEntry entry = ChartTestUtils.numberOne();
        String numberOne = "number one";
        when(delegate.getLine(entry)).thenReturn(numberOne);
        String expected = "[b]" + numberOne + "[/b]";

        String formettedLine = formatter.getLine(entry);
        assertEquals(expected, formettedLine);
    }
}
