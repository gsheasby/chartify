package chart.spotify;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.wrapper.spotify.Api;
import com.wrapper.spotify.exceptions.WebApiException;
import com.wrapper.spotify.methods.ArtistSearchRequest;
import com.wrapper.spotify.methods.PlaylistTracksRequest;
import com.wrapper.spotify.methods.TrackRequest;
import com.wrapper.spotify.methods.TrackSearchRequest;
import com.wrapper.spotify.methods.TracksRequest;
import com.wrapper.spotify.methods.UserPlaylistsRequest;
import com.wrapper.spotify.models.Album;
import com.wrapper.spotify.models.Artist;
import com.wrapper.spotify.models.ClientCredentials;
import com.wrapper.spotify.models.Page;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.SimpleAlbum;
import com.wrapper.spotify.models.SimplePlaylist;
import com.wrapper.spotify.models.SimpleTrack;
import com.wrapper.spotify.models.Track;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
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

    public List<PlaylistTrack> getPlaylist(String name) {
        try {
            List<SimplePlaylist> allPlaylists = getAllPlaylists();
            System.out.println(allPlaylists.size());
            allPlaylists.stream().map(SimplePlaylist::getName).sorted().forEach(System.out::println);
            return allPlaylists.stream()
                    .filter(playlist -> playlist.getName().equalsIgnoreCase(name))
                    .findAny()
                    .map(playlist -> getPlaylistTracks(playlist.getId()))
                    .orElseGet(ImmutableList::of);
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't find playlist " + name, e);
        }
    }

    public List<SimplePlaylist> getAllPlaylists() throws IOException, WebApiException {
        List<SimplePlaylist> playlists = Lists.newArrayList();
        int limit= 50;
        int offset = 0;
        do {
            UserPlaylistsRequest request = api
                    .getPlaylistsForUser(config.userName())
                    .limit(limit)
                    .offset(offset)
                    .build();
            Page<SimplePlaylist> playlistPage = request.get();
            playlists.addAll(playlistPage.getItems());
            offset += limit;
        } while (playlists.size() >= offset);
        return playlists;

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

    Optional<SimpleTrack> searchForTrack(String title, Artist artist) {
        Page<SimpleAlbum> albumPage = getAlbumsForArtist(artist);
        List<SimpleAlbum> albums = albumPage.getItems();
        for (SimpleAlbum simpleAlbum: albums) {
            Album album = getAlbum(simpleAlbum);
            Optional<SimpleTrack> maybeTrack = album.getTracks().getItems().stream().filter(st -> st.getName().equalsIgnoreCase(title)).findAny();
            if (maybeTrack.isPresent()) {
                return maybeTrack;
            }
        }

        return Optional.empty();
    }

    private Page<SimpleAlbum> getAlbumsForArtist(Artist artist) {
        try {
            return api.getAlbumsForArtist(artist.getId()).build().get();
        } catch (WebApiException | IOException e) {
            throw new RuntimeException("Error getting albums for artist with name " + artist.getName(), e);
        }
    }

    private Album getAlbum(SimpleAlbum simpleAlbum) {
        try {
            return api.getAlbum(simpleAlbum.getId()).build().get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Error getting album with name " + simpleAlbum.getName(), e);
        }
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

    List<Track> searchForTrack(String title, String artist) {
        return getSearchResultsFromSpotify(title, artist).getItems();
    }

    private Page<Track> getSearchResultsFromSpotify(String title, String artist) {
        TrackSearchRequest request = api.searchTracks(title + " " + artist).build();

        try {
            return request.get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't get track " + title, e);
        }
    }

    public Artist getArtist(String name) {
        return Iterables.getFirst(searchForArtist(name), null);
    }

    List<Artist> searchForArtist(String name) {
        return getArtistSearchResultsFromSpotify(name).getItems();
    }

    private Page<Artist> getArtistSearchResultsFromSpotify(String name) {
        ArtistSearchRequest request = api.searchArtists(name).build();
        try {
            return request.get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't get artist " + name, e);
        }
    }

    public List<PlaylistTrack> getPlaylistTracks(SimplePlaylist playlist) {
        return getPlaylistTracks(playlist.getId());
    }

    List<PlaylistTrack> getPlaylistTracks(String playlistId) {
        List<PlaylistTrack> tracks = Lists.newArrayList();
        int limit = 100;
        int offset = 0;
        do {
            Page<PlaylistTrack> playlistTrackPage = getPlaylistTrackPage(playlistId, limit, offset);
            tracks.addAll(playlistTrackPage.getItems());
            offset += limit;
        } while (tracks.size() >= offset);
        return tracks;
    }

    private Page<PlaylistTrack> getPlaylistTrackPage(String playlistId, int limit, int offset) {
        PlaylistTracksRequest request = api.getPlaylistTracks(config.userName(), playlistId)
                .limit(limit)
                .offset(offset)
                .build();

        try {
            return request.get();
        } catch (WebApiException | IOException e) {
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
            return api
                    .clientCredentialsGrant()
                    .scopes(ImmutableList.of("playlist-read-private", "playlist-read-collaborative"))
                    .build().get();
        } catch (IOException | WebApiException e) {
            throw new RuntimeException("Couldn't authenticate to Spotify", e);
        }
    }
}