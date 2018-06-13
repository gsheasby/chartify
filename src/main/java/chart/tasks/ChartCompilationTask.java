package chart.tasks;

import java.io.IOException;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartConfig;
import chart.ChartPrinter;
import chart.FileChartReader;
import chart.SimpleChartReader;
import chart.SpotifyChartReader;
import chart.csv.CsvChartCompiler;
import chart.csv.CsvChartSaver;

public class ChartCompilationTask {

    public static void main(String[] args) throws IOException {
        ChartConfig config = TaskUtils.getConfig();

        SimpleChartReader reader = new SpotifyChartReader(config);
        FileChartReader derivedReader = new FileChartReader(config.csvDestination());
        ChartCompiler compiler = new CsvChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart();

        new CsvChartSaver(config.csvDestination()).saveChart(chart);

        ChartPrinter.print(chart);
    }
}
