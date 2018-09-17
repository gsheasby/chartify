package chart;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import chart.spotify.ChartPosition;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PlainTextChartFormatter implements ChartFormatter {
    @Override
    public String getHeader(SpotifyChart chart) {
        return String.format("Week %d: %s",
                                         chart.week(),
                                         chart.date().toString("EEEE, MMMM dd, yyyy"));
    }

    @Override
    public String getLine(SpotifyChartEntry entry) {
        String s;
        if (entry.lastPosition().isPresent()) {
            s = String.format("%02d (%02d) %d %s - %s [%s]",
                              entry.position(),
                              entry.lastPosition().get(),
                              entry.weeksOnChart(),
                              entry.artist(),
                              entry.title(),
                              getRun(entry.chartRun()));
        } else {
            s = String.format("%02d (NE) %d %s - %s",
                              entry.position(),
                              entry.weeksOnChart(),
                              entry.artist(),
                              entry.title());
        }
        return s;
    }

    @Override
    public String getDropoutText(SpotifyChartEntry entry) {
        return String.format("-- (%02d) %d %s - %s [%s]",
                                 entry.position(),
                                 entry.weeksOnChart(),
                                 entry.artist(),
                                 entry.title(),
                                 getRun(entry.chartRun()));
    }

    @Override
    public String getBubbler(SpotifyChartEntry entry) {
        return String.format("-- (--) %d %s - %s",
                             entry.weeksOnChart(),
                             entry.artist(),
                             entry.title());
    }

    private static String getRun(Set<ChartPosition> chartPositions) {
        return chartPositions.stream()
                .sorted(Comparator.comparingInt(ChartPosition::week))
                .map(pos -> Integer.toString(pos.position()))
                .collect(Collectors.joining(", "));
    }
}
