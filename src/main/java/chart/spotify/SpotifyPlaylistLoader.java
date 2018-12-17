package chart.spotify;

import java.util.List;

import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;

public class SpotifyPlaylistLoader {
    private final SpotifyApi spotifyApi;

    public static SpotifyPlaylistLoader create(SpotifyConfig config) {
        return new SpotifyPlaylistLoader(config);
    }

    private SpotifyPlaylistLoader(SpotifyConfig config) {
        this.spotifyApi = SpotifyApi.create(config);
    }

    public List<PlaylistTrack> load(String playlistId) {
        Playlist playlist = spotifyApi.getPlaylist(playlistId);

        return playlist.getTracks().getItems();
    }
}
