package chart.csv;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartEntry;
import chart.SimpleChart;
import chart.SimpleChartEntry;
import chart.SimpleChartReader;

public class CsvChartCompilerTest {
    private static final SimpleChartEntry SIMPLE_ENTRY = ImmutableCsvSimpleChartEntry.builder()
                                                                                     .title("title")
                                                                                     .artist("artist")
                                                                                     .position(1)
                                                                                     .build();
    private static final ChartEntry CHART_ENTRY = ImmutableCsvChartEntry.builder()
                                                                           .position(1)
                                                                           .title("title")
                                                                           .artist("artist")
                                                                           .weeksOnChart(1)
                                                                           .build();
    private static final CsvSimpleChartEntry OTHER_CSV_ENTRY = ImmutableCsvSimpleChartEntry.builder()
                                                                                           .title("other-title")
                                                                                           .artist("other-artist")
                                                                                           .position(1)
                                                                                           .build();
    private static final SimpleChart OTHER_CHART = ImmutableCsvSimpleChart.builder()
                                                                          .week(2)
                                                                          .date(DateTime.now())
                                                                          .addEntries(OTHER_CSV_ENTRY)
                                                                          .build();
    private static final DateTime DEFAULT_DATE = new DateTime(2017, 1, 1, 0, 0);

    SimpleChartReader reader = mock(FileChartReader.class);
    FileChartReader derivedReader = mock(FileChartReader.class);
    private CsvChartCompiler compiler;

    @Before
    public void setUp() {
        compiler = new CsvChartCompiler(reader, derivedReader);

    }

    @Test
    public void canCompileChart() throws IOException {
        FileChartReader realReader = new FileChartReader("src/test/resources/charts");
        new CsvChartCompiler(realReader, realReader).compileChart(1);
    }

    @Test
    public void firstMockedWeekHasNewEntries() throws IOException {
        when(reader.findChart(1)).thenReturn(defaultSimpleChart(1));
        when(reader.findChart(0)).thenThrow(IllegalArgumentException.class);

        when(derivedReader.findDerivedChart(0)).thenThrow(IllegalArgumentException.class);
        Chart chart = compiler.compileChart(1);
        assertEquals(1, chart.entries().size());

        assertEquals(CHART_ENTRY, chart.entries().get(0));
    }

    @Test
    public void secondWeekRecordsPreviousPosition() throws IOException {
        when(reader.findChart(1)).thenReturn(defaultSimpleChart(1));
        when(reader.findChart(2)).thenReturn(defaultSimpleChart(2));

        when(derivedReader.findDerivedChart(1)).thenReturn(defaultChart(1));

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
        when(reader.findChart(2)).thenReturn(defaultSimpleChart(2));
        when(derivedReader.findDerivedChart(1)).thenReturn(defaultChart(1));

        Chart chart = compiler.compileChart(2);

        assertEquals(DEFAULT_DATE.plusWeeks(1), chart.date());
    }

    @Test
    public void dropoutsAreRecorded() throws IOException {
        when(reader.findChart(1)).thenReturn(defaultSimpleChart(1));
        when(reader.findChart(2)).thenReturn(OTHER_CHART);
        when(derivedReader.findDerivedChart(1)).thenReturn(defaultChart(1));

        Chart chart = compiler.compileChart(2);

        assertEquals(1, chart.dropouts().size());
    }

    @Test
    public void offsetsAreRespected() throws IOException {
        when(reader.findChart(1)).thenReturn(defaultSimpleChart(1));
        when(reader.findChart(2)).thenReturn(OTHER_CHART);

        when(derivedReader.findDerivedChart(1)).thenReturn(defaultChart(1));
        ChartCompiler compiler = new CsvChartCompiler(reader, derivedReader, 42);
        Chart chart = compiler.compileChart(2);

        DateTime expected = DEFAULT_DATE.plusDays(42);
        assertEquals(expected, chart.date());
    }

    private SimpleChart defaultSimpleChart(int week) {
        return ImmutableCsvSimpleChart.builder()
                                   .week(week)
                                   .date(DEFAULT_DATE)
                                   .addEntries((CsvSimpleChartEntry) SIMPLE_ENTRY)
                                   .build();
    }

    private CsvChart defaultChart(int week) {
        return ImmutableCsvChart.builder()
                                .date(DEFAULT_DATE)
                                .week(week)
                                .addEntries((CsvChartEntry) CHART_ENTRY)
                                .dropouts(new ArrayList<>())
                                .build();
    }
}
