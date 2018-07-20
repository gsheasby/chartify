package chart.spotify;

import org.immutables.value.Value;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

@JsonSerialize(as = ImmutableYoutubeMapping.class)
@JsonDeserialize(as = ImmutableYoutubeMapping.class)
@Value.Immutable
public abstract class YoutubeMapping {
    public abstract String id();
    public abstract String title();
    public abstract String artist();

    public Track getMappedTrack() {
        Track mappedTrack = new Track();
        mappedTrack.setId(id());
        mappedTrack.setName(title());
        String href = "http://www.youtube.com/watch?v=" + id();
        mappedTrack.setHref(href);
        mappedTrack.setUri(href);

        SimpleArtist artist = new SimpleArtist();
        artist.setName(artist());
        artist.setId(id());
        mappedTrack.setArtists(ImmutableList.of(artist));

        return mappedTrack;
    }
}
