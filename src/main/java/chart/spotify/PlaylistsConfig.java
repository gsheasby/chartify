package chart.spotify;

import java.util.Optional;

import org.immutables.value.Value;

@Value.Immutable
public abstract class PlaylistsConfig {
    public abstract String chart();
    public abstract Optional<String> yec();
}
