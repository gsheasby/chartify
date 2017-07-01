package chart;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ChartReaderTest {
    private static String FOLDER = "src/test/resources";

    private ChartReader reader;

    @Before
    public void setUp() {
        reader = new ChartReader(FOLDER);
    }

    @Test
    public void canFindChart() throws IOException {
        SimpleChart chart = reader.findChart(1);
        assertEquals(1, chart.entries().size());

        SimpleChartEntry expected = ImmutableSimpleChartEntry.builder()
                .position(1)
                .title("title")
                .artist("artist")
                .build();
        assertEquals(expected, chart.entries().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotFindNonExistentChart() throws IOException {
        ChartReader other = new ChartReader(FOLDER);
        other.findChart(1337);
    }
}
