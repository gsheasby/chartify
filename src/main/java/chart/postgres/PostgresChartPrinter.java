package chart.postgres;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;

import chart.ChartEntry;
import chart.ChartFormatter;
import chart.ChartPrinter;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PostgresChartPrinter implements ChartPrinter<SpotifyChart> {
    private static final int CUTOFF = 60;

    private final ChartFormatter formatter;

    public PostgresChartPrinter(ChartFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void print(SpotifyChart chart) {
        printChartHeader(chart);
        System.out.println();

        List<SpotifyChartEntry> dropouts = new ArrayList<>(chart.dropouts());
        for (SpotifyChartEntry entry : chart.entries()) {
            if (entry.position() <= CUTOFF) {
                printEntry(entry);
            } else if (wasInLastWeek(entry)) {
                dropouts.add(entryToDropout(entry));
            } else if (isBubbler(entry)) {
                printBubbler(entry);
            }
        }
        System.out.println();

        dropouts.sort(Comparator.comparing(ChartEntry::position));
        for (SpotifyChartEntry dropout : dropouts) {
            printDropout(dropout);
        }
    }

    private SpotifyChartEntry entryToDropout(SpotifyChartEntry entry) {
        Preconditions.checkArgument(entry.lastPosition().isPresent(),
                                    "Dropout must have been in last week!");

        return SpotifyChartEntry.builder()
                .from(entry)
                .weeksOnChart(entry.weeksOnChart() - 1)
                .position(entry.lastPosition().get())
                .chartRun(entry.chartRun().stream()
                          .filter(pos -> pos.position() <= CUTOFF)
                          .collect(Collectors.toList()))
                .build();
    }

    private boolean wasInLastWeek(SpotifyChartEntry entry) {
        return entry.lastPosition().isPresent() && entry.lastPosition().get() <= CUTOFF;
    }

    private boolean isBubbler(SpotifyChartEntry entry) {
        return entry.chartRun().stream().noneMatch(pos -> pos.position() <= CUTOFF);
    }

    private void printChartHeader(SpotifyChart chart) {
        System.out.println(formatter.getHeader(chart));
    }

    private void printEntry(SpotifyChartEntry entry) {
        System.out.println(formatter.getLine(entry));
    }

    private void printBubbler(SpotifyChartEntry entry) {
        System.out.println(formatter.getBubbler(entry));
    }

    private void printDropout(SpotifyChartEntry entry) {
        System.out.println(formatter.getDropoutText(entry));
    }
}