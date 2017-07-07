package chart;

import java.util.List;

import org.immutables.value.Value;
import org.joda.time.DateTime;

@Value.Immutable
public abstract class SimpleChart {
    public abstract int week();
    public abstract DateTime date();
    public abstract List<SimpleChartEntry> entries();
}
