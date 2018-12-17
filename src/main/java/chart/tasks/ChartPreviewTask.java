package chart.tasks;

import java.io.IOException;

import chart.BasicChartPrinter;
import chart.Chart;
import chart.ChartCompiler;
import chart.ChartConfig;
import chart.csv.CsvChartCompiler;
import chart.csv.FileChartReader;
import chart.spotify.SpotifyChartReader;

public class ChartPreviewTask {
    public static void main(String[] args) throws IOException {
        ChartConfig config = TaskUtils.getConfig();

        SpotifyChartReader reader = SpotifyChartReader.chartReader(config);
        FileChartReader derivedReader = new FileChartReader(config.csvDestination());
        ChartCompiler compiler = new CsvChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart();

        new BasicChartPrinter().print(chart);
    }
}
