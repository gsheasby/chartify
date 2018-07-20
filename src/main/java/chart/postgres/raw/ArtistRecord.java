package chart.postgres.raw;

import org.immutables.value.Value;

import com.wrapper.spotify.models.SimpleArtist;

@Value.Immutable
public abstract class ArtistRecord {
    public abstract String id();
    public abstract String name();
    public abstract String href();
    public abstract String uri();

    @Value.Default
    public boolean is_youtube() {
        return false;
    }

    @Value.Derived
    public SimpleArtist simpleArtist() {
        SimpleArtist artist = new SimpleArtist();
        artist.setId(id());
        artist.setName(name());
        artist.setHref(href());
        artist.setUri(uri());
        return artist;
    }

    public static ArtistRecord from(SimpleArtist artist, boolean isYoutube) {
        return ImmutableArtistRecord.builder()
                .id(artist.getId())
                .name(artist.getName())
                .href(artist.getHref())
                .uri(artist.getUri())
                .is_youtube(isYoutube)
                .build();
    }
}
