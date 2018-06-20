package chart.csv;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import chart.SimpleChart;
import chart.SimpleChartReader;
import chart.csv.CsvSimpleChartEntry;
import chart.csv.FileChartReader;
import chart.csv.ImmutableCsvSimpleChartEntry;

public class FileChartReaderTest {
    private static String FOLDER = "src/test/resources/charts";

    private SimpleChartReader reader;

    @Before
    public void setUp() {
        reader = new FileChartReader(FOLDER);
    }

    @Test
    public void canFindChart() throws IOException {
        SimpleChart chart = reader.findChart(1);
        assertEquals(1, chart.entries().size());

        CsvSimpleChartEntry expected = ImmutableCsvSimpleChartEntry.builder()
                                                                   .position(1)
                                                                   .title("title")
                                                                   .artist("artist")
                                                                   .build();
        assertEquals(expected, chart.entries().get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotFindNonExistentChart() throws IOException {
        SimpleChartReader other = new FileChartReader(FOLDER);
        other.findChart(1337);
    }
}
