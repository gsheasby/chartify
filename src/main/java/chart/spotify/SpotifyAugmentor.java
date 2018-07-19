package chart.spotify;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.VisibleForTesting;
import com.wrapper.spotify.models.Track;

import chart.ChartEntry;

public class SpotifyAugmentor {
    private final SpotifyApi api;

    public static SpotifyAugmentor create(SpotifyConfig config) {
        SpotifyApi api = SpotifyApi.create(config);
        return new SpotifyAugmentor(api);
    }

    @VisibleForTesting
    SpotifyAugmentor(SpotifyApi api) {
        this.api = api;
    }

    public List<SpotifyChartEntry> augmentList(List<? extends ChartEntry> chartEntries) {
        return chartEntries.stream().map(this::augment).collect(Collectors.toList());
    }

    public SpotifyChartEntry augment(ChartEntry entry) {
        Track track = api.getTrack(entry.id());

        return ImmutableSpotifyChartEntry.builder()
                                         .from(entry)
                                         .track(track)
                                         .build();
    }
}
