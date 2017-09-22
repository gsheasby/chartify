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

        int chartSize = getChartSize(args);

        ChartReader reader = new SpotifyChartReader(chartSize);
        ChartReader derivedReader = new FileChartReader(CsvConstants.DERIVED_FOLDER);

        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(week);

        ChartPrinter.print(chart);
    }

    private static int getChartSize(String[] args) {
        int defaultSize = 75;
        if (args.length > 1) {
            try {
                return Integer.parseInt(args[1]);
            } catch (NumberFormatException ex) {
                System.err.println(String.format(
                        "Illegal argument %s; using default chart size %d",
                        args[1],
                        defaultSize));
            }
        }
        return defaultSize;
    }
}
