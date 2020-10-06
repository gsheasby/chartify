package chart.tasks;

import chart.ChartConfig;
import chart.DualChartSaver;
import chart.csv.CsvChartSaver;
import chart.format.ChartFormatters;
import chart.postgres.PostgresChartCompiler;
import chart.postgres.PostgresChartHistoryPrinter;
import chart.postgres.PostgresChartPrinter;
import chart.postgres.PostgresChartReader;
import chart.postgres.PostgresChartSaver;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartReader;

import java.io.IOException;
import java.sql.SQLException;

public class ChartCompilationTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ChartConfig config = TaskUtils.getConfig();
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);

        PostgresChartReader postgresChartReader = new PostgresChartReader(connection);
        SpotifyChartReader reader = SpotifyChartReader.chartReader(config);
        PostgresChartCompiler postgresCompiler = new PostgresChartCompiler(reader, postgresChartReader);
        SpotifyChart chart = postgresCompiler.compileChart();

        PostgresChartHistoryPrinter historyPrinter = new PostgresChartHistoryPrinter(connection);
        new PostgresChartPrinter(ChartFormatters.forum(), historyPrinter).print(chart);

        CsvChartSaver csvChartSaver = new CsvChartSaver(config.csvDestination());
        PostgresChartSaver chartSaver = new PostgresChartSaver(connection);
        new DualChartSaver(csvChartSaver, chartSaver).saveChart(chart);
    }
}
