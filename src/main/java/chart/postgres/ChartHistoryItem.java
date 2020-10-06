package chart.postgres;

import chart.postgres.raw.TrackRecord;
import chart.spotify.ChartPosition;
import org.immutables.value.Value;

import java.util.Collection;

@Value.Immutable
public abstract class ChartHistoryItem {
    public abstract TrackRecord track();
    public abstract Collection<ChartPosition> chartRun();

    @Value.Derived
    public int getEntryWeek() {
        return chartRun().stream()
                .map(ChartPosition::week)
                .min(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("Empty chart run!"));
    }

    public static ImmutableChartHistoryItem.Builder builder() {
        return ImmutableChartHistoryItem.builder();
    }
}
