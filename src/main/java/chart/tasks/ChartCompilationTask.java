package chart.tasks;

import java.io.IOException;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartConfig;
import chart.ChartPrinter;
import chart.csv.FileChartReader;
import chart.spotify.SpotifyChartReader;
import chart.csv.CsvChartCompiler;
import chart.csv.CsvChartSaver;

public class ChartCompilationTask {

    public static void main(String[] args) throws IOException {
        ChartConfig config = TaskUtils.getConfig();

        SpotifyChartReader reader = new SpotifyChartReader(config);
        FileChartReader derivedReader = new FileChartReader(config.csvDestination());
        ChartCompiler compiler = new CsvChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart();
        ChartPrinter.print(chart);

//        try {
//            PostgresChartCompiler postgresCompiler = new PostgresChartCompiler(reader, derivedReader);
//            SpotifyChart spotifyChart = postgresCompiler.compileChart();
//            PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
//            PostgresConnection connection = new PostgresConnection(manager);
//            PostgresChartSaver chartSaver = new PostgresChartSaver(connection);
//            chartSaver.saveChart(spotifyChart);
//
//        } catch (SQLException | ClassNotFoundException e) {
//            e.printStackTrace();
//        }

        new CsvChartSaver(config.csvDestination()).saveChart(chart);

    }
}
