package chart;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

public class ChartCompilerTest {
    @Test
    public void canCompileChart() throws IOException {
        ChartReader reader = new ChartReader("src/test/resources");
        ChartCompiler compiler = new ChartCompiler(reader);
        compiler.compileChart(1);
    }

    // TODO add mocking
    @Test
    public void firstWeekHasNewEntries() throws IOException {
        ChartReader reader = new ChartReader("src/test/resources");
        ChartCompiler compiler = new ChartCompiler(reader);
        Chart chart = compiler.compileChart(1);
        assertEquals(1, chart.entries().size());

        ChartEntry expected = ImmutableChartEntry.builder()
                .position(1)
                .title("title")
                .artist("artist")
                .weeksOnChart(1)
                .build();
        assertEquals(expected, chart.entries().get(0));
    }
}
