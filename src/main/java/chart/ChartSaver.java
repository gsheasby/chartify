package chart;

import java.io.IOException;

public interface ChartSaver<T extends Chart> {
    void saveChart(T chart) throws IOException;
}
