package chart;

import java.io.IOException;
import java.util.List;

import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.PlaylistTrack;

public class SpotifyPlaylistLoader {
    private final SpotifyConfig config;

    public SpotifyPlaylistLoader() {
        // TODO load from config file
        this.config = ImmutableSpotifyConfig.builder()
                .clientId("your ID here")
                .clientSecret("it's a secret!")
                .userName("your username")
                .playlistId("your playlist's ID")
                .build();
    }

    public SpotifyPlaylistLoader(SpotifyConfig config) {
        this.config = config;
    }

    public List<PlaylistTrack> load() {
        Api api = getApi();
        PlaylistRequest request = api.getPlaylist(config.userName(), config.playlistId()).build();

        try {
            return request.get().getTracks().getItems();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't load Spotify playlist", e);
        }
    }

    private Api getApi() {
        Api api = Api.builder()
                     .clientId(config.clientId())
                     .clientSecret(config.clientSecret())
                     .build();

        ClientCredentials clientCredentials = getClientCredentials(api);

        api.setAccessToken(clientCredentials.getAccessToken());
        return api;
    }

    private ClientCredentials getClientCredentials(Api api) {
        try {
            return api.clientCredentialsGrant().build().get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't authenticate to Spotify", e);
        }
    }
}
