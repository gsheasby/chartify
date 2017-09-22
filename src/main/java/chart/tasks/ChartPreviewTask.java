package chart.tasks;

import java.io.IOException;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartPrinter;
import chart.ChartReader;
import chart.CsvConstants;
import chart.FileChartReader;
import chart.SpotifyChartReader;

public class ChartPreviewTask {
    public static void main(String[] args) throws IOException {
        int week = Integer.parseInt(args[0]);

        ChartReader reader = new SpotifyChartReader(75);
        ChartReader derivedReader = new FileChartReader(CsvConstants.DERIVED_FOLDER);

        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(week);

        ChartPrinter.print(chart);
    }
}
