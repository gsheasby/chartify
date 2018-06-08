package chart;

import java.io.IOException;

public interface SimpleChartReader<T extends SimpleChart> {
    T findChart(int week) throws IOException;
}
