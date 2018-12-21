package chart.spotify;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class SpotifyConfigTest {
    @Test
    public void canDeserialiseMappings() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        SpotifyConfig config = mapper.readValue(
                new File("src/test/resources/config/spotify-mappings.yml"),
                SpotifyConfig.class);

        assertEquals(1, config.mappings().size());

        Map.Entry<String, YoutubeMapping> entry = Iterables.getOnlyElement(config.mappings().entrySet());
        assertEquals("wasd345", entry.getKey());
        assertEquals("abcd123", entry.getValue().id());
        assertEquals("Song, Title", entry.getValue().title());
        assertEquals("The People", entry.getValue().artist());

        PlaylistsConfig expected = ImmutablePlaylistsConfig.builder()
                .chart("qux")
                .yec("fizz")
                .yecSections(Lists.newArrayList("boom", "bust"))
                .build();

        assertEquals(expected, config.playlists());
    }

}
