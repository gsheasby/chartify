package chart;

import java.util.List;

import org.immutables.value.Value;
import org.joda.time.DateTime;

@Value.Immutable
public abstract class Chart {
    public abstract int week();
    public abstract DateTime date();
    public abstract List<ChartEntry> entries();
    public abstract List<ChartEntry> dropouts();
}
