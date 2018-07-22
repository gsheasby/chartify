package chart.spotify;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.postgres.raw.ArtistRecord;
import chart.postgres.raw.ImmutableArtistRecord;

public class YoutubeMappingTest {
    @Test
    public void canConvertArtistIntoArtistRecord() {
        YoutubeMapping mapping = ImmutableYoutubeMapping.builder()
                .title("Title")
                .artist("Artist")
                .id("id")
                .build();

        Track track = mapping.getMappedTrack();
        SimpleArtist artist = Iterables.getOnlyElement(track.getArtists());
        ArtistRecord record = ArtistRecord.from(artist, true);

        ArtistRecord expected = ImmutableArtistRecord.builder()
                .id("id")
                .name("Artist")
                .href("")
                .uri("")
                .is_youtube(true)
                .build();

        assertEquals(expected, record);
    }

}