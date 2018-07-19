package chart.spotify;

import java.util.HashMap;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.Maps;

@JsonDeserialize(as = ImmutableSpotifyConfig.class)
@JsonSerialize(as = ImmutableSpotifyConfig.class)
@Value.Immutable
public abstract class SpotifyConfig {
    public abstract String clientId();
    public abstract String clientSecret();
    public abstract String userName();
    public abstract String playlistId();

    @Value.Default
    // Using a HashMap explicitly to work around https://github.com/immutables/immutables/issues/680
    public HashMap<String, YoutubeMapping> mappings() {
        return Maps.newHashMap();
    }
}
