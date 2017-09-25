package chart;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(as = ImmutableChartConfig.class)
@JsonSerialize(as = ImmutableChartConfig.class)
@Value.Immutable
public abstract class ChartConfig {
    public abstract SpotifyConfig spotifyConfig();
    public abstract int chartSize();
    public abstract String csvDestination();
}
