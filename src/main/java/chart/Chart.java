package chart;

import java.util.List;

import org.immutables.value.Value;

@Value.Immutable
public abstract class Chart {
    public abstract List<ChartEntry> entries();
}
