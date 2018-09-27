package chart.spotify;

import chart.ChartEntry;
import com.wrapper.spotify.models.Track;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  For importing from CSV files in the older format, without ID/Href/Uri.
 *  In this case, we must search Spotify for both the artist and the title.
 */
public class TrackSearchingAugmentor implements SpotifyAugmentor {
    private final SpotifyApi api;

    public static TrackSearchingAugmentor create(SpotifyApi api) {
        return new TrackSearchingAugmentor(api);
    }

    private TrackSearchingAugmentor(SpotifyApi api) {
        this.api = api;
    }

    @Override
    public List<SpotifyChartEntry> augmentList(List<? extends ChartEntry> chartEntries) {
        return chartEntries.stream().map(this::augment).collect(Collectors.toList());
    }

    @Override
    public SpotifyChartEntry augment(ChartEntry entry) {
        Track track = api.getTrack(entry.title(), entry.artist());
        return SpotifyChartEntry.builder().from(entry).track(track).build();
    }
}
