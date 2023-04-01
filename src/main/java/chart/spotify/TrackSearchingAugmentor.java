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
import java.util.UUID;
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
        String artistName = entry.artist();
        Optional<ArtistRecord> artist = getArtistInvariantToThe(artistName);

        if (!artist.isPresent() && artistName.contains(",")) {
            artist = connection.getArtist(artistName.split(",")[0]);
        }

        if (artist.isPresent()) {
            String id = artist.get().id();
            Optional<TrackRecord> maybeTrack = connection.getTrack(entry.title(), id);

            // TODO if not, return artist and then search spotify better.
            if (maybeTrack.isPresent()) {
                SimpleArtist simpleArtist = artist.get().simpleArtist();
                Track track = maybeTrack.get().track(ImmutableList.of(simpleArtist));

                SpotifyChartEntry spotifyChartEntry = SpotifyChartEntry.builder().from(entry).track(track).build();

                printTrackFound(track, "Found track from postgres");
                return Optional.of(spotifyChartEntry);
            }
        }

        return Optional.empty();
    }

    private Optional<ArtistRecord> getArtistInvariantToThe(String artistName) {
        Optional<ArtistRecord> artist = connection.getArtist(artistName);
        if (!artist.isPresent()) {
            // If starting with 'The', try removing it
            if (artistName.startsWith("The ")) {
                String artistWithoutThe = artistName.substring(4);
                artist = connection.getArtist(artistWithoutThe);
            } else {
                artist = connection.getArtist("The " + artistName);
            }
        }
        return artist;
    }

    private SpotifyChartEntry fetchFromSpotify(ChartEntry entry) {
        String title = entry.title();
        String artist = entry.artist();

        Optional<Track> maybeTrack = getTrack(title, artist);
        if (!maybeTrack.isPresent() && artist.contains(",")) {
            maybeTrack = getTrack(title, artist.split(",")[0]);
        }

        Track track = maybeTrack.orElseGet(() -> createTrack(title, artist));

        return SpotifyChartEntry.builder().from(entry).track(track).build();
    }

    private Optional<Track> getTrack(String title, String artist) {
        Optional<Track> track = spotifySearcher.searchForTrack(title, artist);
        track.ifPresent(value -> printTrackFound(value, "Found track from spotify"));
        return track;
    }

    private void printTrackFound(Track track, String message) {
        System.out.println(String.format(message + ": (%s) %s - %s",
                track.getId(),
                track.getArtists().get(0).getName(),
                track.getName()));
    }

    private Track createTrack(String title, String artist) {
        getArtist(artist);

        Track track = new Track();
        track.setId(UUID.randomUUID().toString());
        track.setName(title);
        track.setArtists(ImmutableList.of(getArtist(artist)));
        printTrackFound(track, "Created track");
        return track;
    }

    private SimpleArtist getArtist(String artist) {
        Optional<SimpleArtist> postgresArtist = connection.getArtist(artist)
                .map(ArtistRecord::simpleArtist);
        return postgresArtist
                .orElseGet(() -> loadFromSpotify(artist));

    }

    private SimpleArtist loadFromSpotify(String artist) {
        Optional<SimpleArtist> simpleArtist = spotifySearcher.searchForArtist(artist);
        if (simpleArtist.isPresent()) {
            return simpleArtist.get();
        }

        if (artist.contains(",")) {
            String[] artists = artist.split(",");
            String firstArtist = artists[0];
            return spotifySearcher.searchForArtist(firstArtist)
                    .orElseGet(() -> createArtist(firstArtist));
        } else {
            return createArtist(artist);
        }
    }

    private SimpleArtist createArtist(String name) {
        // TEMP - want to catch this case initially
        throw new RuntimeException("Couldn't find artist " + name);

//        SimpleArtist simpleArtist = new SimpleArtist();
//        simpleArtist.setId(UUID.randomUUID().toString());
//        simpleArtist.setName(name);
//        return simpleArtist;
    }

}
