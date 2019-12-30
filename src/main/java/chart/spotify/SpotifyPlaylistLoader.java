package chart.spotify;

import com.google.common.collect.Lists;
import com.wrapper.spotify.models.Playlist;
import com.wrapper.spotify.models.PlaylistTrack;
import com.wrapper.spotify.models.Track;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpotifyPlaylistLoader {
    private final SpotifyApi spotifyApi;
    private final SpotifyConfig spotifyConfig;

    public static SpotifyPlaylistLoader create(SpotifyConfig config) {
        return new SpotifyPlaylistLoader(config);
    }

    private SpotifyPlaylistLoader(SpotifyConfig config) {
        this.spotifyApi = SpotifyApi.create(config);
        this.spotifyConfig = config;
    }

    public List<SimpleSpotifyChartEntry> loadChartEntries(String playlistId) {
        List<PlaylistTrack> playlist = load(playlistId);
        return convertToEntries(playlist, Optional.empty());
    }

    public List<SimpleSpotifyChartEntry> loadChartEntries(String playlistId, int limit) {
        List<PlaylistTrack> playlist = load(playlistId);
        return convertToEntries(playlist, Optional.of(limit));
    }

    // TODO this only loads the first 100 tracks in the playlist - need extra calls to load later items.
    private List<PlaylistTrack> load(String playlistId) {
        Playlist playlist = spotifyApi.getPlaylist(playlistId);
        return playlist.getTracks().getItems();
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private List<SimpleSpotifyChartEntry> convertToEntries(List<PlaylistTrack> playlist,
                                                           Optional<Integer> limit) {
        Stream<PlaylistTrack> trackStream = limit
                .map(integer -> playlist.stream().limit(integer))
                .orElseGet(playlist::stream);
        List<Track> tracks = trackStream
                .map(PlaylistTrack::getTrack)
                .collect(Collectors.toList());
        int position = 1;
        List<SimpleSpotifyChartEntry> entries = Lists.newArrayList();
        for (Track track : tracks) {
            SimpleSpotifyChartEntry entry = createEntry(position, track);
            position += 1;
            entries.add(entry);
        }
        return entries;
    }

    private SimpleSpotifyChartEntry createEntry(int position, Track track) {
        if (spotifyConfig.mappings().containsKey(track.getId())) {
            Track mappedTrack = spotifyConfig.mappings().get(track.getId()).getMappedTrack();
            return ImmutableSimpleSpotifyChartEntry.builder()
                                                   .position(position)
                                                   .track(mappedTrack)
                                                   .isYoutube(true)
                                                   .build();
        } else {
            return ImmutableSimpleSpotifyChartEntry.builder()
                                                   .position(position)
                                                   .track(track)
                                                   .isYoutube(false)
                                                   .build();
        }
    }
}
