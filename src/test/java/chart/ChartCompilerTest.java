package chart;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.joda.time.DateTime;
import org.junit.Test;

public class ChartCompilerTest {
    private static final SimpleChartEntry SIMPLE_ENTRY = ImmutableSimpleChartEntry.builder()
                                                                                  .title("title")
                                                                                  .artist("artist")
                                                                                  .position(1)
                                                                                  .build();
    private static final SimpleChart SIMPLE_CHART = ImmutableSimpleChart.builder()
                                                                        .week(1)
                                                                        .date(DateTime.now())
                                                                        .addEntries(SIMPLE_ENTRY)
                                                                        .build();
    private static final ImmutableChartEntry CHART_ENTRY = ImmutableChartEntry.builder()
                                                                              .position(1)
                                                                              .title("title")
                                                                              .artist("artist")
                                                                              .weeksOnChart(1)
                                                                              .build();

    @Test
    public void canCompileChart() throws IOException {
        ChartReader reader = new ChartReader("src/test/resources");
        ChartReader derivedReader = reader; // TODO
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        compiler.compileChart(1);
    }

    @Test
    public void firstMockedWeekHasNewEntries() throws IOException {
        ChartReader reader = mock(ChartReader.class);
        when(reader.findChart(1)).thenReturn(SIMPLE_CHART);
        when(reader.findChart(0)).thenThrow(IllegalArgumentException.class);

        ChartReader derivedReader = reader; // TODO
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(1);
        assertEquals(1, chart.entries().size());

        assertEquals(CHART_ENTRY, chart.entries().get(0));
    }

    @Test
    public void secondWeekRecordsPreviousPosition() throws IOException {
        ChartReader reader = mock(ChartReader.class);
        when(reader.findChart(1)).thenReturn(SIMPLE_CHART);
        when(reader.findChart(2)).thenReturn(SIMPLE_CHART);

        ChartReader derivedReader = reader; // TODO
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(2);
        assertEquals(1, chart.entries().size());

        ChartEntry expected = ImmutableChartEntry.builder()
                .artist("artist")
                .lastPosition(1)
                .position(1)
                .title("title")
                .weeksOnChart(2)
                .build();

        assertEquals(expected, chart.entries().get(0));
    }
}
