package chart.csv;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import chart.Chart;
import chart.ChartSaver;

public class CsvChartSaverTest {
    String folder = "src/test/resources/temp";
    String file = "src/test/resources/temp/123-20170816.csv";

    @Before
    public void setUp() throws IOException {
        Files.deleteIfExists(Paths.get(file));
        Files.deleteIfExists(Paths.get(folder));
    }

    @Test
    public void create() throws IOException {
        ChartSaver saver = new CsvChartSaver(folder);
        Chart chart = mock(Chart.class);
        when(chart.date()).thenReturn(DateTime.now());
        when(chart.week()).thenReturn(123);
        saver.saveChart(chart);
    }

}