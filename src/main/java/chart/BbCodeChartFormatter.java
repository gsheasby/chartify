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
        if (entry.position() == 1) {
            return "[b]" + rawLine + "[/b]";
        } else {
            return rawLine;
        }
    }

    @Override
    public String getDropoutText(SpotifyChartEntry entry) {
        return delegate.getDropoutText(entry);
    }
}
