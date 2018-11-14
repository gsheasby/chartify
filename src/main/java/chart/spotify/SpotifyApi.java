package chart.spotify;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.ArtistSearchRequest;
import com.wrapper.spotify.methods.PlaylistRequest;
import com.wrapper.spotify.methods.TrackRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.TracksRequest;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.Track;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class SpotifyApi {
    private final Api api;
    private final SpotifyConfig config;

    public static SpotifyApi create(SpotifyConfig config) {
        Api api = getApi(config);
        return new SpotifyApi(api, config);
    }

    private SpotifyApi(Api api, SpotifyConfig config) {
        this.api = api;
        this.config = config;
    }

    Track getTrack(String trackId) {
        TrackRequest request = api.getTrack(trackId).build();
        try {
            return request.get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't get track with ID " + trackId, e);
        }
    }

    List<Track> getTracks(List<String> trackIds) {
        List<List<String>> lists = Lists.partition(trackIds, 50); // Spotify API limits us to 50 trackIds per request
        return lists.stream().map(this::requestTracks).flatMap(List::stream).collect(Collectors.toList());
    }

    private List<Track> requestTracks(List<String> trackIds) {
        Preconditions.checkArgument(trackIds.size() <= 50, "Can't request more than 50 artists!");
        TracksRequest request = api.getTracks(trackIds).build();
        try {
            return request.get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't get tracks " + trackIds, e);
        }
    }

    Track getTrack(String title, String artist) {
        TrackSearchRequest request = api.searchTracks(title).build();
        try {
            Page<Track> trackPage = request.get();
            for (Track item : trackPage.getItems()) {
                boolean sameTitle = item.getName().equalsIgnoreCase(title);
                boolean sameArtist = item.getArtists().stream().anyMatch(a -> a.getName().equalsIgnoreCase(artist));
                if (sameTitle && sameArtist) {
                    return item;
                }
                // TODO have some measure of edit distance?
            }
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't get track " + title, e);
        }

        throw new IllegalStateException(String.format(
                "Couldn't find exact matches for track %s by %s", title, artist));
    }

    Artist getArtist(String name) throws IOException, WebApiException {
        ArtistSearchRequest request = api.searchArtists(name).build();
        List<Artist> items = request.get().getItems();
        List<Artist> exactMatches = items.stream()
                                         .filter(artist -> artist.getName().equalsIgnoreCase(name))
                                         .collect(Collectors.toList());

        if (exactMatches.isEmpty()) {
            throw new IllegalStateException("Couldn't find any exact match for artist" + name);
        }
        if (exactMatches.size() > 1) {
            System.out.println("Multiple matches found; returning the first result");
        }

        return exactMatches.get(0);
    }

    Playlist getPlaylist(String playlistId) {
        PlaylistRequest request = api.getPlaylist(config.userName(), playlistId).build();

        try {
            return request.get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't load Spotify playlist", e);
        }
    }

    private static Api getApi(SpotifyConfig config) {
        Api api = Api.builder()
                     .clientId(config.clientId())
                     .clientSecret(config.clientSecret())
                     .build();

        ClientCredentials clientCredentials = getClientCredentials(api);

        api.setAccessToken(clientCredentials.getAccessToken());
        return api;
    }

    private static ClientCredentials getClientCredentials(Api api) {
        try {
            return api.clientCredentialsGrant().build().get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't authenticate to Spotify", e);
        }
    }
}