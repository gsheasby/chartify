package chart;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(as = ImmutableSpotifyConfig.class)
@JsonSerialize(as = ImmutableSpotifyConfig.class)
@Value.Immutable
public abstract class SpotifyConfig {
    public abstract String clientId();
    public abstract String clientSecret();
    public abstract String userName();
    public abstract String playlistId();
}
