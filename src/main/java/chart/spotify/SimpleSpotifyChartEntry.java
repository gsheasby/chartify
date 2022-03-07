package chart.spotify;

import com.wrapper.spotify.models.SimpleArtist;
import org.immutables.value.Value;

import com.wrapper.spotify.models.Track;

import chart.SimpleChartEntry;

import java.util.stream.Collectors;

@Value.Immutable
public abstract class SimpleSpotifyChartEntry implements SimpleChartEntry {
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
        return track().getArtists().stream()
                .map(SimpleArtist::getName)
                .collect(Collectors.joining(", "));
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
}
