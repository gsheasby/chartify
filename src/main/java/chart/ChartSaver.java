package chart;

import java.io.IOException;

public interface ChartSaver {
    void saveChart(Chart chart) throws IOException;
}
