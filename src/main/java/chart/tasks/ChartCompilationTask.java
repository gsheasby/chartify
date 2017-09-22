package chart.tasks;

import java.io.IOException;

import com.wrapper.spotify.exceptions.WebApiException;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartPrinter;
import chart.ChartReader;
import chart.ChartSaver;
import chart.FileChartReader;
import chart.SpotifyChartReader;

public class ChartCompilationTask {
    private static final String FOLDER = "src/main/resources";
    private static final String DERIVED_FOLDER = FOLDER + "/derived";

    public static void main(String[] args) throws IOException, WebApiException {
        int week = Integer.parseInt(args[0]);
        ChartReader reader = new SpotifyChartReader();
        ChartReader derivedReader = new FileChartReader(DERIVED_FOLDER);
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(week);

        new ChartSaver(DERIVED_FOLDER).saveChart(chart);

        ChartPrinter.print(chart);
    }
}
