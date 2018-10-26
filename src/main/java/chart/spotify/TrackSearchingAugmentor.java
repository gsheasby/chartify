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
 *  In this case, we must search Spotify for both the artist and the title.
 */
public class TrackSearchingAugmentor implements SpotifyAugmentor {
    private final SpotifyApi api;
    private final PostgresConnection connection;

    public static TrackSearchingAugmentor create(SpotifyApi api, PostgresConnection connection) {
        return new TrackSearchingAugmentor(api, connection);
    }

    private TrackSearchingAugmentor(SpotifyApi api, PostgresConnection connection) {
        this.api = api;
        this.connection = connection;
    }

    @Override
    public List<SpotifyChartEntry> augmentList(List<? extends ChartEntry> chartEntries) {
        return chartEntries.stream().map(this::augment).collect(Collectors.toList());
    }

    @Override
    public SpotifyChartEntry augment(ChartEntry entry) {
        Optional<ArtistRecord> artist = connection.getArtist(entry.artist());

        if (artist.isPresent()) {
            String id = artist.get().id();
            Optional<TrackRecord> maybeTrack = connection.getTrack(entry.title(), id);
            if (maybeTrack.isPresent()) {
                SimpleArtist simpleArtist = artist.get().simpleArtist();
                Track track = maybeTrack.get().track(ImmutableList.of(simpleArtist));

                return SpotifyChartEntry.builder().from(entry).track(track).build();
            }
        }

        Track track = api.getTrack(entry.title(), entry.artist());
        return SpotifyChartEntry.builder().from(entry).track(track).build();
    }
}
