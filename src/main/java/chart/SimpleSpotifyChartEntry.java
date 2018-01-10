package chart;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SimpleSpotifyChartEntry implements SimpleChartEntry {
    public abstract String id();
    public abstract String href();
    public abstract String uri();
}
