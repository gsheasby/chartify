package chart.spotify;

import java.util.List;
import java.util.stream.Collectors;

import org.immutables.value.Value;

import com.google.common.base.Objects;
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

    @Override
    public boolean equals(Object another) {
        if (this == another) return true;
        return another instanceof ImmutableSpotifyChartEntry
                && equalTo((ImmutableSpotifyChartEntry) another);
    }

    private boolean equalTo(ImmutableSpotifyChartEntry another) {
        return position().equals(another.position())
                && Objects.equal(lastPosition(), another.lastPosition())
                && weeksOnChart().equals(another.weeksOnChart())
                && title().equalsIgnoreCase(another.title())
                && artist().equalsIgnoreCase(another.artist())
                && id().equals(another.id())
                && href().equals(another.href())
                && uri().equals(another.uri());
    }

    public static List<SpotifyChartEntry> fromList(List<? extends ChartEntry> chartEntries) {
        return chartEntries.stream()
                           .map(SpotifyChartEntry::from)
                           .collect(Collectors.toList());
    }

    public static SpotifyChartEntry from(ChartEntry entry) {
        SimpleArtist artist = new SimpleArtist();
        artist.setName(entry.artist());
        Track track = new Track();
        track.setName(entry.title());
        track.setArtists(ImmutableList.of(artist));
        track.setId(entry.id());
        track.setHref(entry.href());
        track.setUri(entry.uri());

        return ImmutableSpotifyChartEntry.builder()
                .from(entry)
                .track(track)
                .build();
    }
}
