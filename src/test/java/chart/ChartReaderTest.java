package chart;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ChartReaderTest {
    private static String FOLDER = "src/main/resources";

    private ChartReader reader;

    @Before
    public void setUp() {
        reader = new ChartReader(FOLDER, 589);
    }

    @Test
    public void canFindChart() throws IOException {
        assertTrue(reader.findChart());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotFindNonExistentChart() throws IOException {
        ChartReader other = new ChartReader(FOLDER, 1337);
        other.findChart();
    }
}
