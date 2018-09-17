package chart.postgres;

import java.util.ArrayList;
import java.util.List;

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
                dropouts.add(entry);
            } else if (isBubbler(entry)) {
                printBubbler(entry);
            }
        }
        System.out.println();

        for (SpotifyChartEntry dropout : dropouts) {
            printDropout(dropout);
        }
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