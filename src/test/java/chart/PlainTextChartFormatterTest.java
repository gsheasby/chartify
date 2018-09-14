package chart;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PlainTextChartFormatterTest {
    private ChartFormatter formatter;

    @Before
    public void setUp() {
        formatter = new PlainTextChartFormatter();
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
        SpotifyChartEntry entry = ChartTestUtils.newEntry();

        String expected = "01 (NE) 1 artist - title";
        String line = formatter.getLine(entry);
        assertEquals(expected, line);
    }

    @Test
    public void lastPositionFormat() {
        SpotifyChartEntry entry = ChartTestUtils.threeWeeks();

        String expected = "05 (07) 3 artist - title [10, 7, 5]";
        String line = formatter.getLine(entry);
        assertEquals(expected, line);
    }

    @Test
    public void dropoutFormat() {
        SpotifyChartEntry entry = ChartTestUtils.threeWeeks();

        String expected = "-- (05) 3 artist - title [10, 7, 5]";
        String line = formatter.getDropoutText(entry);
        assertEquals(expected, line);
    }
}