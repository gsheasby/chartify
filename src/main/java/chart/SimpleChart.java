package chart;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SimpleChart {
    public abstract List<SimpleChartEntry> entries();
}
