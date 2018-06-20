package chart.spotify;

import java.util.List;

import org.immutables.value.Value;

import chart.Chart;

@Value.Immutable
public abstract class SpotifyChart implements Chart {
    @Override
    public abstract List<SpotifyChartEntry> entries();

    @Override
    public abstract List<SpotifyChartEntry> dropouts();
}
