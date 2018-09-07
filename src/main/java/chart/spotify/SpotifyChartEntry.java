package chart.spotify;

import java.util.Set;

import org.immutables.value.Value;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.wrapper.spotify.models.Track;

import chart.ChartEntry;

@Value.Immutable
public abstract class SpotifyChartEntry implements ChartEntry {
    // ids are unique, so equality can be established using other fields
    // Also, Track does not have a custom equals() method.
    @Value.Auxiliary
    public abstract Track track();

    @Value.Default
    public boolean isYoutube() {
        return false;
    }

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

    @Value.Auxiliary
    @Value.Default
    public Set<ChartPosition> chartRun() {
        return ImmutableSet.of();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("position", position())
                          .add("lastPosition", lastPosition())
                          .add("weeksOnChart", weeksOnChart())
                          .add("title", title())
                          .add("artist", artist())
                          .add("id", id())
                          .add("href", href())
                          .add("url", uri())
                          .add("isYoutube", isYoutube())
                          .toString();
    }

    @Override
    public int hashCode() {
        int h = 5381;
        h += (h << 5) + position().hashCode();
        h += (h << 5) + Objects.hashCode(lastPosition());
        h += (h << 5) + weeksOnChart().hashCode();
        h += (h << 5) + track().hashCode();
        h += (h << 5) + (isYoutube() ? 1 : 0);
        return h;
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
                && uri().equals(another.uri())
                && isYoutube() == another.isYoutube();
    }

    public static ImmutableSpotifyChartEntry.Builder builder() {
        return ImmutableSpotifyChartEntry.builder();
    }
}
