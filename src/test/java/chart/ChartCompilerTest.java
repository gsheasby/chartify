package chart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Test;

import chart.csv.CsvSimpleChart;
import chart.csv.CsvSimpleChartEntry;
import chart.csv.ImmutableCsvSimpleChart;
import chart.csv.ImmutableCsvSimpleChartEntry;

public class ChartCompilerTest {
    private static final CsvSimpleChartEntry SIMPLE_ENTRY = ImmutableCsvSimpleChartEntry.builder()
                                                                                        .title("title")
                                                                                        .artist("artist")
                                                                                        .position(1)
                                                                                        .build();
    private static final CsvChartEntry CHART_ENTRY = ImmutableCsvChartEntry.builder()
                                                                              .position(1)
                                                                              .title("title")
                                                                              .artist("artist")
                                                                              .weeksOnChart(1)
                                                                              .build();
    private static final CsvSimpleChartEntry OTHER_ENTRY = ImmutableCsvSimpleChartEntry.builder()
                                                                                       .title("other-title").artist("other-artist").position(1).build();
    private static final CsvSimpleChart OTHER_CHART = ImmutableCsvSimpleChart.builder()
                                                                             .week(2).date(DateTime.now()).addEntries(OTHER_ENTRY).build();
    public static final DateTime DEFAULT_DATE = new DateTime(2017, 1, 1, 0, 0);

    @Test
    public void canCompileChart() throws IOException {
        FileChartReader reader = new FileChartReader("src/test/resources");
        FileChartReader derivedReader = reader; // TODO
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        compiler.compileChart(1);
    }

    @Test
    public void firstMockedWeekHasNewEntries() throws IOException {
        SimpleChartReader reader = mock(FileChartReader.class);
        when(reader.findChart(1)).thenReturn(defaultSimpleChart(1));
        when(reader.findChart(0)).thenThrow(IllegalArgumentException.class);

        FileChartReader derivedReader = mock(FileChartReader.class);
        when(derivedReader.findDerivedChart(0)).thenThrow(IllegalArgumentException.class);
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(1);
        assertEquals(1, chart.entries().size());

        assertEquals(CHART_ENTRY, chart.entries().get(0));
    }

    @Test
    public void secondWeekRecordsPreviousPosition() throws IOException {
        SimpleChartReader reader = mock(FileChartReader.class);
        when(reader.findChart(1)).thenReturn(defaultSimpleChart(1));
        when(reader.findChart(2)).thenReturn(defaultSimpleChart(2));

        FileChartReader derivedReader = mock(FileChartReader.class);
        when(derivedReader.findDerivedChart(1)).thenReturn(defaultChart(1));
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(2);
        assertEquals(1, chart.entries().size());

        ChartEntry expected = ImmutableCsvChartEntry.builder()
                .artist("artist")
                .lastPosition(1)
                .position(1)
                .title("title")
                .weeksOnChart(2)
                .build();

        assertEquals(expected, chart.entries().get(0));
    }

    @Test
    public void secondWeekIsSevenDaysAfterFirst() throws IOException {
        SimpleChartReader reader = mock(FileChartReader.class);
        when(reader.findChart(2)).thenReturn(defaultSimpleChart(2));

        FileChartReader derivedReader = mock(FileChartReader.class);
        when(derivedReader.findDerivedChart(1)).thenReturn(defaultChart(1));

        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(2);

        assertEquals(DEFAULT_DATE.plusWeeks(1), chart.date());
    }

    @Test
    public void dropoutsAreRecorded() throws IOException {
        SimpleChartReader reader = mock(FileChartReader.class);
        when(reader.findChart(1)).thenReturn(defaultSimpleChart(1));
        when(reader.findChart(2)).thenReturn(OTHER_CHART);

        FileChartReader derivedReader = mock(FileChartReader.class);
        when(derivedReader.findDerivedChart(1)).thenReturn(defaultChart(1));
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(2);

        assertEquals(1, chart.dropouts().size());
    }

    @Test
    public void offsetsAreRespected() throws IOException {
        SimpleChartReader reader = mock(FileChartReader.class);
        when(reader.findChart(1)).thenReturn(defaultSimpleChart(1));
        when(reader.findChart(2)).thenReturn(OTHER_CHART);

        FileChartReader derivedReader = mock(FileChartReader.class);
        when(derivedReader.findDerivedChart(1)).thenReturn(defaultChart(1));
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader, 42);
        Chart chart = compiler.compileChart(2);

        DateTime expected = DEFAULT_DATE.plusDays(42);
        assertEquals(expected, chart.date());
    }

    private SimpleChart defaultSimpleChart(int week) {
        return ImmutableCsvSimpleChart.builder()
                                   .week(week)
                                   .date(DEFAULT_DATE)
                                   .addEntries(SIMPLE_ENTRY)
                                   .build();
    }

    private CsvChart defaultChart(int week) {
        return ImmutableCsvChart.builder()
                .date(DEFAULT_DATE)
                .week(week)
                .addEntries(CHART_ENTRY)
                .dropouts(new ArrayList<>())
                .build();
    }
}
