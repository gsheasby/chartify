package chart;

import java.io.IOException;

public interface ChartReader<T extends Chart, U extends SimpleChart> extends SimpleChartReader<U> {
    T findLatestChart() throws IOException;

    T findDerivedChart(int week) throws IOException;
}
