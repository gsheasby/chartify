package chart.spotify;

import java.util.ArrayList;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonDeserialize(as = ImmutablePlaylistsConfig.class)
@JsonSerialize(as = ImmutablePlaylistsConfig.class)
@Value.Immutable
public abstract class PlaylistsConfig {
    public abstract String chart();

    public abstract String yec();

    public abstract ArrayList<String> yecSections();
}
