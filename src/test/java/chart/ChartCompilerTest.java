package chart;

import org.junit.Test;

public class ChartCompilerTest {
    @Test
    public void create() {
        ChartReader reader = new ChartReader("foo");
        ChartCompiler compiler = new ChartCompiler(reader);
    }
}
