package chart.tasks;

import chart.ChartConfig;
import chart.format.ChartFormatters;
import chart.postgres.PostgresChartHistoryPrinter;
import chart.postgres.PostgresChartPrinter;
import chart.postgres.PostgresChartReader;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyChart;

import java.io.IOException;
import java.sql.SQLException;

public class HistoricalChartTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        int week = 600;
        if (args.length < 1) {
            System.out.println("Using default week of " + week);
        } else {
            week = Integer.parseInt(args[0]);
        }

        ChartConfig config = TaskUtils.getConfig();

        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        PostgresChartReader reader = new PostgresChartReader(connection);
        SpotifyChart chart = reader.findDerivedChart(week);

        PostgresChartHistoryPrinter historyPrinter = new PostgresChartHistoryPrinter(connection);
        new PostgresChartPrinter(ChartFormatters.preview(), historyPrinter).print(chart);
    }
}
