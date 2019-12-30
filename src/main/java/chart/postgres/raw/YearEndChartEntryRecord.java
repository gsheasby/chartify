package chart.postgres.raw;

import com.google.common.base.Preconditions;
import org.immutables.value.Value;

@Value.Immutable
public abstract class YearEndChartEntryRecord {
    public abstract int year();
    public abstract int position();
    public abstract String track_id();

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder extends ImmutableYearEndChartEntryRecord.Builder {}

    @Value.Check
    protected final void check() {
        Preconditions.checkState(year() > 0, "Year must be positive");
        Preconditions.checkState(position() > 0, "Position must be positive");
    }
}
