package chart;

import org.immutables.value.Value;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import chart.postgres.PostgresConfig;
import chart.spotify.SpotifyConfig;

@JsonDeserialize(as = ImmutableChartConfig.class)
@JsonSerialize(as = ImmutableChartConfig.class)
@Value.Immutable
public abstract class ChartConfig {
    @JsonProperty("spotify")
    public abstract SpotifyConfig spotifyConfig();

    @JsonProperty("postgres")
    public abstract PostgresConfig postgresConfig();

    public abstract int chartSize();
    public abstract String csvDestination();
}
