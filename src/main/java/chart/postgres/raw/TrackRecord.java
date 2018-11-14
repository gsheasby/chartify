package chart.postgres.raw;

import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public abstract class TrackRecord {
    public abstract String id();
    public abstract String name();
    public abstract String href();
    public abstract String uri();
    public abstract boolean is_youtube();

    @Value.Auxiliary
    @Value.Derived
    public Track track(List<SimpleArtist> artists) {
        Track track = new Track();
        track.setId(id());
        track.setName(name());
        track.setHref(href());
        track.setUri(uri());
        track.setArtists(artists);
        return track;
    }

    public static TrackRecord from(Track track, boolean isYoutube) {
        return ImmutableTrackRecord.builder()
                .id(track.getId())
                .name(track.getName())
                .href(track.getHref())
                .uri(track.getUri())
                .is_youtube(isYoutube)
                .build();
    }
}
