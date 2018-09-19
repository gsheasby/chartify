package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;

import chart.ChartConfig;
import chart.ChartFormatters;
import chart.DualChartSaver;
import chart.csv.CsvChartSaver;
import chart.postgres.PostgresChartCompiler;
import chart.postgres.PostgresChartPrinter;
import chart.postgres.PostgresChartReader;
import chart.postgres.PostgresChartSaver;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartReader;

public class ChartCompilationTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ChartConfig config = TaskUtils.getConfig();
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);

        PostgresChartReader postgresChartReader = new PostgresChartReader(connection);
        SpotifyChartReader reader = new SpotifyChartReader(config);
        PostgresChartCompiler postgresCompiler = new PostgresChartCompiler(reader, postgresChartReader);
        SpotifyChart chart = postgresCompiler.compileChart();

        CsvChartSaver csvChartSaver = new CsvChartSaver(config.csvDestination());
        PostgresChartSaver chartSaver = new PostgresChartSaver(connection);
        new PostgresChartPrinter(ChartFormatters.forum()).print(chart);
        new DualChartSaver(csvChartSaver, chartSaver).saveChart(chart);
    }
}
