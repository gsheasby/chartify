package chart.postgres;

import chart.ChartFormatter;
import chart.ChartPrinter;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PostgresChartPrinter implements ChartPrinter<SpotifyChart> {
    private final ChartFormatter formatter;

    public PostgresChartPrinter(ChartFormatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public void print(SpotifyChart chart) {
        printChartHeader(chart);
        System.out.println();

        for (SpotifyChartEntry entry : chart.entries()) {
            printEntry(entry);
        }
        System.out.println();

        for (SpotifyChartEntry dropout : chart.dropouts()) {
            printDropout(dropout);
        }
    }

    private void printChartHeader(SpotifyChart chart) {
        System.out.println(formatter.getHeader(chart));
    }

    private void printEntry(SpotifyChartEntry entry) {
        System.out.println(formatter.getLine(entry));
    }

    private void printDropout(SpotifyChartEntry entry) {
        System.out.println(formatter.getDropoutText(entry));
    }
}