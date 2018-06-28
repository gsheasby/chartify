package chart.spotify;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize(as = ImmutableYoutubeMapping.class)
@JsonDeserialize(as = ImmutableYoutubeMapping.class)
@Value.Immutable
public abstract class YoutubeMapping {
    public abstract String id();
    public abstract String title();
    public abstract String artist();
}
