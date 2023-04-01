package chart.spotify;

import chart.tasks.TaskUtils;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleTrack;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

// Tests likely to fail if playlists change or are deleted! TODO Probably want to replace with mocks.
public class SpotifyApiTest {
    private SpotifyApi api;

    @Before
    public void before() throws IOException {
        SpotifyConfig config = TaskUtils.getConfig().spotifyConfig();
        api = SpotifyApi.create(config);
    }

    @Test
    public void playlistTracks_largePlaylist() throws IOException {
        List<PlaylistTrack> yec2018 = api.getPlaylistTracks("0Y40UtUT8Pq6u5MClCsY3t");
        assertEquals(131, yec2018.size());
    }

    @Test
    public void playlistTracks_shortPlaylist() throws IOException {
        List<PlaylistTrack> yecB = api.getPlaylistTracks("06s71dPNH8jrngIpvZJ30c");
        assertEquals(7, yecB.size());
    }

    @Test
    public void playlistTracks_hundredSongPlaylist() throws IOException {
        List<PlaylistTrack> yec2019 = api.getPlaylistTracks("4owDTor0xREFbWl90xkOlP");
        assertEquals(100, yec2019.size());
    }

    @Test
    public void testRide() throws IOException, WebApiException {
        Artist ride = api.getArtist("Ride");
        assertEquals("Ride", ride.getName());

        Optional<SimpleTrack> trackMatches = api.searchForTrack("All I Want", ride);
        assertTrue(trackMatches.isPresent());
    }

}
