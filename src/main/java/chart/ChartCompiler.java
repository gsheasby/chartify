package chart;

import java.io.IOException;

public interface ChartCompiler<T extends Chart> {
    T compileChart() throws IOException;

    T compileChart(int week) throws IOException;
}
