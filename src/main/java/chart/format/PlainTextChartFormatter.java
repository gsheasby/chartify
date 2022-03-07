package chart.format;

import chart.spotify.ChartPosition;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;
import com.google.common.base.Preconditions;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

public class PlainTextChartFormatter implements ChartFormatter {
    private final boolean printRunsForSongsInChart;

    PlainTextChartFormatter(boolean printRunsForSongsInChart) {
        this.printRunsForSongsInChart = printRunsForSongsInChart;
    }

    @Override
    public String getHeader(SpotifyChart chart) {
        return String.format("Week %d: %s",
                             chart.week(),
                             chart.date().toString("EEEE, MMMM dd, yyyy"));
    }

    @Override
    public String getLine(SpotifyChartEntry entry) {
        return entry.lastPosition().isPresent()
                ? getLineForEntryFromLastWeek(entry)
                : String.format("%02d (NE) %d %s - %s",
                                entry.position(),
                                entry.weeksOnChart(),
                                entry.artist(),
                                entry.title());
    }

    private String getLineForEntryFromLastWeek(SpotifyChartEntry entry) {
        Preconditions.checkArgument(entry.lastPosition().isPresent(), "Expected lastPosition to be present!");
        return printRunsForSongsInChart
                ? printWithChartRun(entry)
                : String.format("%02d (%02d) %d %s - %s",
                                entry.position(),
                                entry.lastPosition().get(),
                                entry.weeksOnChart(),
                                entry.artist(),
                                entry.title());
    }

    private String printWithChartRun(SpotifyChartEntry entry) {
        Integer peak = entry.chartRun().stream().map(ChartPosition::position).min(Integer::compareTo).orElseThrow();
        return  (peak < entry.position())
                ? String.format("%02d (%02d) %d %s - %s [%s] (#%d)",
                        entry.position(),
                        entry.lastPosition().orElseThrow(),
                        entry.weeksOnChart(),
                        entry.artist(),
                        entry.title(),
                        getRun(entry.chartRun()),
                        peak)
                : String.format("%02d (%02d) %d %s - %s [%s]",
                        entry.position(),
                        entry.lastPosition().orElseThrow(),
                        entry.weeksOnChart(),
                        entry.artist(),
                        entry.title(),
                        getRun(entry.chartRun()));
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
