package chart.spotify;

import org.immutables.value.Value;

import com.wrapper.spotify.models.Track;

import chart.SimpleChartEntry;

@Value.Immutable
public abstract class SimpleSpotifyChartEntry implements SimpleChartEntry {
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
}
