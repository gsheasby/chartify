package chart.postgres.raw;

import org.immutables.value.Value;

@Value.Immutable
public abstract class TrackPositionRecord {
    public abstract int position();
    public abstract String track_id();
    public abstract String track_name();
    public abstract String track_href();
    public abstract String track_uri();

    @Value.Default
    public boolean is_youtube() {
        return false;
    }
}
