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

    public static SpotifyChart augment(Chart chart, SpotifyAugmentor augmentor) {
        return ImmutableSpotifyChart.builder()
                             .date(chart.date())
                             .week(chart.week())
                             .entries(augmentor.augmentList(chart.entries()))
                             .dropouts(augmentor.augmentList(chart.dropouts()))
                             .build();
    }
}
