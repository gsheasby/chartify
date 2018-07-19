package chart.spotify;

import java.util.ArrayList;
import java.util.List;

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

    public List<SpotifyChartEntry> fromList(List<? extends ChartEntry> chartEntries) {
        List<SpotifyChartEntry> list = new ArrayList<>();
        for (ChartEntry chartEntry : chartEntries) {
            list.add(from(chartEntry));
        }
        return list;
    }

    public SpotifyChartEntry from(ChartEntry entry) {
        Track track = api.getTrack(entry.id());

        return ImmutableSpotifyChartEntry.builder()
                                         .from(entry)
                                         .track(track)
                                         .build();
    }
}
