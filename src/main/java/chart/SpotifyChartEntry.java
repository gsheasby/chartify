package chart;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SpotifyChartEntry implements ChartEntry {
    public abstract String id();
    public abstract String href();
    public abstract String uri();
}
