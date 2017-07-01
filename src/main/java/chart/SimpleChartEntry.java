package chart;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SimpleChartEntry {
    public abstract int position();
    public abstract String title();
    public abstract String artist();
}
