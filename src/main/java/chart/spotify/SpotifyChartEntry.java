package chart.spotify;

import org.immutables.value.Value;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.ChartEntry;

@Value.Immutable
public abstract class SpotifyChartEntry implements ChartEntry {
    // ids are unique, so equality can be established using other fields
    // Also, Track does not have a custom equals() method.
    @Value.Auxiliary
    public abstract Track track();

    @Override
    public String title() {
        return track().getName();
    }

    @Override
    public String artist() {
        return track().getArtists().get(0).getName();
    }

    @Override
    public String id() {
        return track().getId();
    }

    @Override
    public String href() {
        return track().getHref();
    }

    @Override
    public String uri() {
        return track().getUri();
    }

    public static SpotifyChartEntry from(ChartEntry entry) {
        SimpleArtist artist = new SimpleArtist();
        artist.setName(entry.artist());
        Track track = new Track();
        track.setName(entry.title());
        track.setArtists(ImmutableList.of(artist));

        return ImmutableSpotifyChartEntry.builder()
                .from(entry)
                .track(track)
                .build();
    }
}
