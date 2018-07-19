package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartConfig;
import chart.ChartPrinter;
import chart.ChartReader;
import chart.postgres.PostgresChartCompiler;
import chart.postgres.PostgresChartReader;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyChartReader;

public class PostgresChartPreviewTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ChartConfig config = TaskUtils.getConfig();

        SpotifyChartReader reader = new SpotifyChartReader(config);
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        ChartReader derivedReader = new PostgresChartReader(connection);
        ChartCompiler compiler = new PostgresChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart();

        ChartPrinter.print(chart);
    }
}