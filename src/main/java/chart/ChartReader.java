package chart;

import java.io.IOException;

public interface ChartReader extends SimpleChartReader {
    Chart findLatestChart() throws IOException;

    Chart findDerivedChart(int week) throws IOException;
}
