package chart.postgres.raw;

import org.immutables.value.Value;

@Value.Immutable
public abstract class TrackArtistRecord {
    public abstract String track_id();
    public abstract String artist_id();
}
