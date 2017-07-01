package chart;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ChartEntry {
    public abstract Integer position();
    public abstract Optional<Integer> lastPosition();
    public abstract Integer weeksOnChart();
    public abstract String title();
    public abstract String artist();
}
