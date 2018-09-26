package chart.spotify;

import chart.ChartEntry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.wrapper.spotify.models.Track;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class IdLookupAugmentor implements SpotifyAugmentor {
    private final SpotifyApi api;
    private final SpotifyConfig config;

    public static SpotifyAugmentor create(SpotifyConfig config) {
        SpotifyApi api = SpotifyApi.create(config);
        return new IdLookupAugmentor(api, config);
    }

    @VisibleForTesting
    IdLookupAugmentor(SpotifyApi api, SpotifyConfig config) {
        this.api = api;
        this.config = config;
    }

    @Override
    public List<SpotifyChartEntry> augmentList(List<? extends ChartEntry> chartEntries) {
        Map<Boolean, List<String>> partitionedTracks = chartEntries
                .stream()
                .map(ChartEntry::id)
                .collect(Collectors.partitioningBy(getMappedIds()::contains));

        List<String> trackIds = partitionedTracks.get(false);
        List<Track> spotifyTracks = api.getTracks(trackIds);
        Preconditions.checkState(trackIds.size() == spotifyTracks.size(), "Some tracks were not returned!");

        List<String> youtubeTracks = partitionedTracks.get(true);
        List<Track> tracks = youtubeTracks
                                              .stream()
                                              .map(this::getYoutubeTrack)
                                              .collect(Collectors.toList());
        tracks.addAll(spotifyTracks);

        Map<String, Track> tracksById = tracks.stream()
                                              .collect(Collectors.toMap(Track::getId, track -> track));

        Set<String> youtubeTrackIds = new HashSet<>(youtubeTracks);
        return chartEntries.stream()
                           .map(e -> enrich(e, tracksById, youtubeTrackIds::contains))
                           .collect(Collectors.toList());
    }

    @Override
    public SpotifyChartEntry augment(ChartEntry entry) {
        boolean isYoutube = getMappedIds().contains(entry.id());
        Track track = isYoutube
                ? getYoutubeTrack(entry.id())
                : api.getTrack(entry.id());

        return ImmutableSpotifyChartEntry.builder()
                                         .from(entry)
                                         .track(track)
                                         .isYoutube(isYoutube)
                                         .build();
    }

    private Set<String> getMappedIds() {
        return config.mappings().values().stream().map(YoutubeMapping::id).collect(Collectors.toSet());
    }

    private Track getYoutubeTrack(String id) {
        Optional<YoutubeMapping> mappingForId = config.mappings().values()
                .stream()
                .filter(mapping -> mapping.id().equals(id))
                .findFirst();
        Preconditions.checkArgument(mappingForId.isPresent(), "Mapping for id {} not found!", id);
        YoutubeMapping mapping = mappingForId.get();
        return mapping.getMappedTrack();
    }

    private SpotifyChartEntry enrich(ChartEntry entry,
                                     Map<String, Track> tracksById,
                                     Predicate<String> isYoutube) {
        Track track = tracksById.get(entry.id());

        return ImmutableSpotifyChartEntry.builder()
                                         .from(entry)
                                         .track(track)
                                         .isYoutube(isYoutube.test(entry.id()))
                                         .build();
    }
}
