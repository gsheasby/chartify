package chart.tasks;

import chart.ChartConfig;
import chart.ChartHistoryPrinter;
import chart.postgres.PostgresChartHistoryPrinter;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyApi;

import java.io.IOException;
import java.sql.SQLException;

public class ChartHistoryTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ChartConfig config = TaskUtils.getConfig();

        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        PostgresChartHistoryPrinter printer = new PostgresChartHistoryPrinter(connection);

        SpotifyApi api = SpotifyApi.create(config.spotifyConfig());

        String artist = "Feeder";

        new ChartHistoryPrinter(api, printer).printHistory(artist);

    }

}
