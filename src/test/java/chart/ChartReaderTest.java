package chart;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

    // TODO can't find chart that isn't there
    @Test
    public void canFindChart() {
        assertTrue(reader.findChart());
    }

    @Test
    public void cannotFindNonExistentChart() {
        ChartReader other = new ChartReader(1337);
        assertFalse(other.findChart());
    }
}
