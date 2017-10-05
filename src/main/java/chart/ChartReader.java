package chart;

import java.io.IOException;

public interface ChartReader {
    Chart findLatestChart() throws IOException;

    Chart findDerivedChart(int week) throws IOException;

    SimpleChart findChart(int week) throws IOException;
}
