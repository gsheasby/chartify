package chart.postgres;

import chart.ChartEntry;
import chart.ChartPrinter;
import chart.format.ChartFormatter;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;
import com.google.common.base.Preconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class PostgresChartPrinter implements ChartPrinter<SpotifyChart> {
    private static final int CUTOFF = 60;

    private final ChartFormatter formatter;
    private final PostgresChartHistoryPrinter historyPrinter;

    public PostgresChartPrinter(ChartFormatter formatter, PostgresChartHistoryPrinter historyPrinter) {
        this.formatter = formatter;
        this.historyPrinter = historyPrinter;
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

        System.out.println();
        chart.entries().stream()
                .filter(this::shouldPrintHistory)
                .map(entry -> entry.track().getArtists())
                .flatMap(Collection::stream)
                .forEach(historyPrinter::printHistory);
    }

    private boolean shouldPrintHistory(SpotifyChartEntry entry) {
        return !entry.lastPosition().isPresent() && entry.position() <= CUTOFF;
    }

    private void printChartHeader(SpotifyChart chart) {
        System.out.println(formatter.getHeader(chart));
    }

    private void printEntry(SpotifyChartEntry entry) {
        System.out.println(formatter.getLine(entry));
    }

    private boolean wasInLastWeek(SpotifyChartEntry entry) {
        return entry.lastPosition().isPresent() && entry.lastPosition().get() <= CUTOFF;
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

    private boolean isBubbler(SpotifyChartEntry entry) {
        return entry.chartRun().stream().noneMatch(pos -> pos.position() <= CUTOFF);
    }

    private void printBubbler(SpotifyChartEntry entry) {
        System.out.println(formatter.getBubbler(entry));
    }

    private void printDropout(SpotifyChartEntry entry) {
        System.out.println(formatter.getDropoutText(entry));
    }
}
