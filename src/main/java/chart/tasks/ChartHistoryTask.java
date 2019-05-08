package chart.tasks;

import chart.ChartConfig;
import chart.postgres.PostgresChartHistoryPrinter;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.SimpleArtist;

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

        try {
            Artist spotifyArtist = api.getArtist(artist);

            SimpleArtist simpleArtist = new SimpleArtist();
            simpleArtist.setId(spotifyArtist.getId());
            simpleArtist.setName(spotifyArtist.getName());
            simpleArtist.setHref(spotifyArtist.getHref());
            simpleArtist.setUri(spotifyArtist.getUri());

            printer.printHistory(simpleArtist);
        } catch (WebApiException e) {
            e.printStackTrace();
        }

    }
}
