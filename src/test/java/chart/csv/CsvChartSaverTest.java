package chart.csv;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import chart.Chart;
import chart.ChartSaver;
import chart.CsvChart;

public class CsvChartSaverTest {
    String folder = "src/test/resources/temp";

    @Before
    public void setUp() throws IOException {
        FileUtils.deleteDirectory(Paths.get(folder).toFile());
    }

    @Test
    public void create() throws IOException {
        ChartSaver saver = new CsvChartSaver(folder);
        Chart chart = mock(CsvChart.class);
        when(chart.date()).thenReturn(DateTime.now());
        when(chart.week()).thenReturn(123);
        saver.saveChart(chart);
    }

}