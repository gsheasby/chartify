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
        return delegate.getLine(entry);
    }

    @Override
    public String getDropoutText(SpotifyChartEntry entry) {
        return delegate.getDropoutText(entry);
    }
}
