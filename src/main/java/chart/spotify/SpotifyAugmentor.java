package chart.spotify;

import chart.ChartEntry;

import java.util.List;

public interface SpotifyAugmentor {
    List<SpotifyChartEntry> augmentList(List<? extends ChartEntry> chartEntries);

    SpotifyChartEntry augment(ChartEntry entry);
}
