package chart;

import chart.postgres.PostgresChartHistoryPrinter;
import chart.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.SimpleArtist;

import java.io.IOException;

public class ChartHistoryPrinter {
    private final SpotifyApi api;
    private final PostgresChartHistoryPrinter printer;

    public ChartHistoryPrinter(SpotifyApi api, PostgresChartHistoryPrinter printer) {
        this.api = api;
        this.printer = printer;
    }

    public void printHistory(String artist) throws IOException {
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
