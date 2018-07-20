package chart.spotify;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.wrapper.spotify.models.Track;

import chart.ChartEntry;

public class SpotifyAugmentor {
    private final SpotifyApi api;
    private final SpotifyConfig config;

    public static SpotifyAugmentor create(SpotifyConfig config) {
        SpotifyApi api = SpotifyApi.create(config);
        return new SpotifyAugmentor(api, config);
    }

    @VisibleForTesting
    SpotifyAugmentor(SpotifyApi api, SpotifyConfig config) {
        this.api = api;
        this.config = config;
    }

    public List<SpotifyChartEntry> augmentList(List<? extends ChartEntry> chartEntries) {
        Map<Boolean, List<String>> partitionedTracks = chartEntries
                .stream()
                .map(ChartEntry::id)
                .collect(Collectors.partitioningBy(getMappedIds()::contains));

        List<String> trackIds = partitionedTracks.get(false);
        List<Track> spotifyTracks = api.getTracks(trackIds);
        Preconditions.checkState(trackIds.size() == spotifyTracks.size(), "Some tracks were not returned!");

        List<Track> tracks = partitionedTracks.get(true)
                .stream()
                .map(this::getYoutubeTrack)
                .collect(Collectors.toList());
        tracks.addAll(spotifyTracks);

        Map<String, Track> tracksById = tracks.stream().collect(Collectors.toMap(Track::getId, track -> track));

        return chartEntries.stream().map(e -> enrich(e, tracksById)).collect(Collectors.toList());
    }

    public SpotifyChartEntry augment(ChartEntry entry) {
        Track track = getMappedIds().contains(entry.id())
                ? getYoutubeTrack(entry.id())
                : api.getTrack(entry.id());

        return ImmutableSpotifyChartEntry.builder()
                                         .from(entry)
                                         .track(track)
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

    private SpotifyChartEntry enrich(ChartEntry entry, Map<String, Track> tracksById) {
        Track track = tracksById.get(entry.id());

        return ImmutableSpotifyChartEntry.builder()
                                         .from(entry)
                                         .track(track)
                                         .build();
    }
}
