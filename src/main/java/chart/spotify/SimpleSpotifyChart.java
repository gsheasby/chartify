package chart.spotify;

import java.util.List;

import org.immutables.value.Value;

import chart.SimpleChart;

@Value.Immutable
public abstract class SimpleSpotifyChart implements SimpleChart {
    @Override
    public abstract List<SimpleSpotifyChartEntry> entries();
}
