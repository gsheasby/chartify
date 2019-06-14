package chart;

import chart.postgres.PostgresChartHistoryPrinter;
import chart.spotify.SpotifyApi;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.SimpleArtist;

import java.io.IOException;
import java.util.Optional;

public class ChartHistoryPrinter {
    private final SpotifyApi api;
    private final PostgresChartHistoryPrinter printer;

    public ChartHistoryPrinter(SpotifyApi api, PostgresChartHistoryPrinter printer) {
        this.api = api;
        this.printer = printer;
    }

    public void printHistory(String artist) throws IOException {
        Optional<Artist> maybeSpotifyArtist = getArtist(artist);

        if (!maybeSpotifyArtist.isPresent()) {
            System.out.println("Not printing history for " + artist + " - Spotify lookup failed.");
            return;
        }

        printHistory(maybeSpotifyArtist.get());
    }

    private Optional<Artist> getArtist(String artist) throws IOException {
        try {
            return Optional.of(api.getArtist(artist));
        } catch (WebApiException e) {
            System.out.println("Couldn't load artist from Spotify!");
            return Optional.empty();
        }
    }

    private void printHistory(Artist spotifyArtist) {
        SimpleArtist simpleArtist = new SimpleArtist();
        simpleArtist.setId(spotifyArtist.getId());
        simpleArtist.setName(spotifyArtist.getName());
        simpleArtist.setHref(spotifyArtist.getHref());
        simpleArtist.setUri(spotifyArtist.getUri());

        printer.printHistory(simpleArtist);
    }
}
