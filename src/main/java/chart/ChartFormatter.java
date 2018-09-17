package chart;

import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public interface ChartFormatter {
    String getHeader(SpotifyChart chart);

    String getLine(SpotifyChartEntry entry);

    String getDropoutText(SpotifyChartEntry entry);

    String getBubbler(SpotifyChartEntry entry);
}
