package chart;

import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class BbCodeChartFormatter implements ChartFormatter {
    private final ChartFormatter delegate;

    public BbCodeChartFormatter(ChartFormatter delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getHeader(SpotifyChart chart) {
        return delegate.getHeader(chart);
    }

    @Override
    public String getLine(SpotifyChartEntry entry) {
        String rawLine = delegate.getLine(entry);
        return shouldBeInBold(entry) ? bold(rawLine) : rawLine;
    }

    @Override
    public String getDropoutText(SpotifyChartEntry entry) {
        return delegate.getDropoutText(entry);
    }

    private boolean shouldBeInBold(SpotifyChartEntry entry) {
        return entry.position() == 1 || !entry.lastPosition().isPresent();
    }

    private String bold(String rawLine) {
        return "[b]" + rawLine + "[/b]";
    }
}
