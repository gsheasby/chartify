package chart.postgres;

import java.io.IOException;

import chart.Chart;
import chart.ChartSaver;

public class PostgresChartSaver implements ChartSaver {
    @Override
    public void saveChart(Chart chart) throws IOException {
        // TODO richer Chart
//        chart.entries().stream().map(entry -> entry.artist());
    }
}
