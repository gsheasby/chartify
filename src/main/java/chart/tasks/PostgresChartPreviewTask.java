package chart.tasks;

import chart.ChartConfig;
import chart.format.ChartFormatters;
import chart.postgres.PostgresChartCompiler;
import chart.postgres.PostgresChartHistoryPrinter;
import chart.postgres.PostgresChartPrinter;
import chart.postgres.PostgresChartReader;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartReader;

import java.io.IOException;
import java.sql.SQLException;

public class PostgresChartPreviewTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ChartConfig config = TaskUtils.getConfig();

        SpotifyChartReader reader = SpotifyChartReader.chartReader(config);
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        PostgresChartReader derivedReader = new PostgresChartReader(connection);
        PostgresChartCompiler compiler = new PostgresChartCompiler(reader, derivedReader);
        SpotifyChart chart = compiler.compileChart();

        PostgresChartHistoryPrinter historyPrinter = new PostgresChartHistoryPrinter(connection);
        new PostgresChartPrinter(ChartFormatters.preview(), historyPrinter).print(chart);
    }
}
