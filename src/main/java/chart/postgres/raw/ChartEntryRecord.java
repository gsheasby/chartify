package chart.postgres.raw;

import org.immutables.value.Value;

@Value.Immutable
public abstract class ChartEntryRecord {
    public abstract int chart_week();
    public abstract int position();
    public abstract String track_id();
}
