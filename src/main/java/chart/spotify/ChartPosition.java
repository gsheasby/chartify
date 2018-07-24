package chart.spotify;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ChartPosition {
    public abstract int week();
    public abstract int position();
}
