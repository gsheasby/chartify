package chart.tasks;

import java.io.IOException;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartPrinter;
import chart.ChartReader;
import chart.ChartSaver;
import chart.CsvConstants;
import chart.FileChartReader;
import chart.SpotifyChartReader;

public class ChartCompilationTask {

    public static void main(String[] args) throws IOException {
        int week = Integer.parseInt(args[0]);

        // Options
        int chartSize = getChartSize(args);
        String lastWeekSource = CsvConstants.TOP_75_FOLDER;

        ChartReader reader = new SpotifyChartReader(chartSize);
        ChartReader derivedReader = new FileChartReader(lastWeekSource);
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(week);

        new ChartSaver(lastWeekSource).saveChart(chart);

        ChartPrinter.print(chart);
    }

    // TODO copied from ChartPreviewTask. If both tasks coexist, consider extracting an ABC.
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
