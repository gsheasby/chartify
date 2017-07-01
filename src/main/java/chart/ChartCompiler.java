package chart;

import java.io.IOException;

public class ChartCompiler {
    private final ChartReader reader;

    public ChartCompiler(ChartReader reader) {
        this.reader = reader;
    }

    public void compileChart(int week) throws IOException {
        SimpleChart thisWeek = reader.findChart(week);
    }
}
