package chart.spotify;

import chart.ChartEntry;
import chart.postgres.PostgresConnection;
import chart.postgres.raw.ArtistRecord;
import chart.postgres.raw.TrackRecord;
import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *  For importing from CSV files in the older format, without ID/Href/Uri.
 *  In this case, we must search Postgres or Spotify for both the artist and the title.
 */
public class TrackSearchingAugmentor implements SpotifyAugmentor {
    private final PostgresConnection connection;
    private final SpotifySearcher spotifySearcher;

    public static TrackSearchingAugmentor create(SpotifyApi api, PostgresConnection connection) {
        SpotifySearcher spotifySearcher = new SpotifySearcher(api);
        return new TrackSearchingAugmentor(connection, spotifySearcher);
    }

    private TrackSearchingAugmentor(PostgresConnection connection, SpotifySearcher spotifySearcher) {
        this.connection = connection;
        this.spotifySearcher = spotifySearcher;
    }

    @Override
    public List<SpotifyChartEntry> augmentList(List<? extends ChartEntry> chartEntries) {
        return chartEntries.stream().map(this::augment).collect(Collectors.toList());
    }

    @Override
    public SpotifyChartEntry augment(ChartEntry entry) {
        return fetchFromPostgres(entry).orElseGet(() -> fetchFromSpotify(entry));
    }

    private Optional<SpotifyChartEntry> fetchFromPostgres(ChartEntry entry) {
        Optional<SpotifyChartEntry> postgresEntry = Optional.empty();
        Optional<ArtistRecord> artist = connection.getArtist(entry.artist());

        if (artist.isPresent()) {
            String id = artist.get().id();
            Optional<TrackRecord> maybeTrack = connection.getTrack(entry.title(), id);
            if (maybeTrack.isPresent()) {
                SimpleArtist simpleArtist = artist.get().simpleArtist();
                Track track = maybeTrack.get().track(ImmutableList.of(simpleArtist));

                SpotifyChartEntry spotifyChartEntry = SpotifyChartEntry.builder().from(entry).track(track).build();
                postgresEntry = Optional.of(spotifyChartEntry);
            }
        }
        return postgresEntry;
    }

    private SpotifyChartEntry fetchFromSpotify(ChartEntry entry) {
        // TODO handle cases where track was removed from Spotify
        String title = entry.title();
        String artist = entry.artist();
        Track track = getTrack(title, artist);
        System.out.println(String.format("Found track from spotify: (%s) %s - %s",
                track.getId(),
                track.getArtists().get(0).getName(),
                track.getName()));
        return SpotifyChartEntry.builder().from(entry).track(track).build();
    }

    private Track getTrack(String title, String artist) {
        Optional<Track> bestMatch = spotifySearcher.searchForTrack(title, artist);

        return bestMatch.get();
    }

}
