package chart.spotify;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class PlaylistsConfigTest {
    @Test
    public void testSede() throws IOException {
        ArrayList<String> sections = Lists.newArrayList("baz", "qux");
        PlaylistsConfig config = ImmutablePlaylistsConfig.builder()
                .chart("foo")
                .yec("bar")
                .yecSections(sections)
                .build();

        String jsonConfig = "{" +
                "\"chart\":\"foo\"," +
                "\"yec\":\"bar\"," +
                "\"yecSections\":[\"baz\",\"qux\"]" +
                "}";

        ObjectMapper mapper = new ObjectMapper();

        PlaylistsConfig deserialised = mapper.readValue(jsonConfig, PlaylistsConfig.class);

        String serialised = mapper.writeValueAsString(config);

        assertEquals(jsonConfig, serialised);
        assertEquals(config, deserialised);
    }
}
