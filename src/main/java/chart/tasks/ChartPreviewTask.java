package chart.tasks;

import java.io.IOException;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartConfig;
import chart.ChartPrinter;
import chart.FileChartReader;
import chart.SpotifyChartReader;
import chart.postgres.PostgresChartCompiler;

public class ChartPreviewTask {
    public static void main(String[] args) throws IOException {
        ChartConfig config = TaskUtils.getConfig();

        SpotifyChartReader reader = new SpotifyChartReader(config);
        FileChartReader derivedReader = new FileChartReader(config.csvDestination());
        ChartCompiler compiler = new PostgresChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart();

        ChartPrinter.print(chart);
    }
}
