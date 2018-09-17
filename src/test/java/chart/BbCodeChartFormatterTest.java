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
        assertGetLineReturnsBold(ChartTestUtils.numberOne());
    }

    @Test
    public void newEntryShouldBeBold() {
        assertGetLineReturnsBold(ChartTestUtils.newEntry());
    }

    @Test
    public void riserOutsideTopThirtyShouldBeBold() {
        assertGetLineReturnsBold(ChartTestUtils.entry(33, 42));
    }

    @Test
    public void riserInsideTopThirtyShouldNotBeBold() {
        String foo = "foo";
        SpotifyChartEntry entry = ChartTestUtils.entry(13, 14);
        when(delegate.getLine(entry)).thenReturn(foo);
        String formattedLine = formatter.getLine(entry);
        assertEquals(foo, formattedLine);
    }

    @Test
    public void riserIntoTopThirtyShouldBeBold() {
        assertGetLineReturnsBold(ChartTestUtils.entry(13, 42));
    }

    @Test
    public void getsBubblerTextFromDelegate() {
        String foo = "foo";
        SpotifyChartEntry entry = ChartTestUtils.entry(63, 64);
        when(delegate.getBubbler(entry)).thenReturn(foo);

        String formattedLine = formatter.getBubbler(entry);

        assertEquals(foo, formattedLine);
        verify(delegate, times(1)).getBubbler(entry);
    }

    private void assertGetLineReturnsBold(SpotifyChartEntry entry) {
        String foo = "foo";
        when(delegate.getLine(entry)).thenReturn(foo);
        String expected = "[b]" + foo + "[/b]";

        String formattedLine = formatter.getLine(entry);
        assertEquals(expected, formattedLine);
    }
}
