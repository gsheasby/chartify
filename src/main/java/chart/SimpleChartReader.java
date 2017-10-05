package chart;

import java.io.IOException;

public interface SimpleChartReader {
    SimpleChart findChart(int week) throws IOException;
}
