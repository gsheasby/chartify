package chart.spotify;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.wrapper.spotify.models.SimpleArtist;
import com.wrapper.spotify.models.Track;

import chart.ChartEntry;

public class SpotifyAugmentor {
    private final SpotifyConfig config;

    public SpotifyAugmentor(SpotifyConfig config) {
        this.config = config;
    }

    public List<SpotifyChartEntry> fromList(List<? extends ChartEntry> chartEntries) {
        return chartEntries.stream()
                           .map(this::from)
                           .collect(Collectors.toList());
    }

    public SpotifyChartEntry from(ChartEntry entry) {
        SimpleArtist artist = new SimpleArtist();
        artist.setName(entry.artist());
        Track track = new Track();
        track.setName(entry.title());
        track.setArtists(ImmutableList.of(artist));
        track.setId(entry.id());
        track.setHref(entry.href());
        track.setUri(entry.uri());

        return ImmutableSpotifyChartEntry.builder()
                                         .from(entry)
                                         .track(track)
                                         .build();
    }
}
