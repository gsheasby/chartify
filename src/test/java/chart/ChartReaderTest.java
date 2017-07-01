package chart;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class ChartReaderTest {
    private ChartReader reader;

    @Before
    public void setUp() {
        reader = new ChartReader(589);
    }

    @Test
    public void create() {
    }

    @Test
    public void canFindChart() throws IOException {
        assertTrue(reader.findChart());
    }

    @Test
    public void cannotFindNonExistentChart() throws IOException {
        ChartReader other = new ChartReader(1337);
        assertFalse(other.findChart());
    }
}
