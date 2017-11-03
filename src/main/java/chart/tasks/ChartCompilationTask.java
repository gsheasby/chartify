package chart.tasks;

import java.io.IOException;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartConfig;
import chart.ChartPrinter;
import chart.ChartReader;
import chart.ChartSaver;
import chart.FileChartReader;
import chart.SimpleChartReader;
import chart.SpotifyChartReader;

public class ChartCompilationTask {

    public static void main(String[] args) throws IOException {
        ChartConfig config = TaskUtils.getConfig();

        SimpleChartReader reader = new SpotifyChartReader(config);
        ChartReader derivedReader = new FileChartReader(config.csvDestination());
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart();

        new ChartSaver(config.csvDestination()).saveChart(chart);

        ChartPrinter.print(chart);
    }
}
