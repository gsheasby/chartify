package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;

import chart.ChartConfig;
import chart.ChartFormatter;
import chart.postgres.PostgresChartCompiler;
import chart.postgres.PostgresChartPrinter;
import chart.postgres.PostgresChartReader;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartReader;

public class PostgresChartPreviewTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ChartConfig config = TaskUtils.getConfig();

        SpotifyChartReader reader = new SpotifyChartReader(config);
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        PostgresChartReader derivedReader = new PostgresChartReader(connection);
        PostgresChartCompiler compiler = new PostgresChartCompiler(reader, derivedReader);
        SpotifyChart chart = compiler.compileChart();

        new PostgresChartPrinter(new ChartFormatter()).print(chart);
    }
}
