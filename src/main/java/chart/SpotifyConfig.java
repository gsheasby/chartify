package chart;

import org.immutables.value.Value;

@Value.Immutable
public abstract class SpotifyConfig {
    public abstract String clientId();
    public abstract String clientSecret();
    public abstract String userName();
    public abstract String playlistId();
}
