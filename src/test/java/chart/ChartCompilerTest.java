package chart;

import java.io.IOException;

import org.junit.Test;

public class ChartCompilerTest {
    @Test
    public void canCompileChart() throws IOException {
        ChartReader reader = new ChartReader("src/test/resources");
        ChartCompiler compiler = new ChartCompiler(reader);
        compiler.compileChart(1);
    }
}
