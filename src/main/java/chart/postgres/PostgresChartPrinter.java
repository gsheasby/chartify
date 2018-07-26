package chart.postgres;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import chart.ChartPrinter;
import chart.spotify.ChartPosition;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PostgresChartPrinter implements ChartPrinter<SpotifyChart> {

    @Override
    public void print(SpotifyChart chart) {
        System.out.println(String.format("Week %d: %s",
                                         chart.week(),
                                         chart.date().toString("EEEE, MMMM dd, yyyy")));
        System.out.println();

        for (SpotifyChartEntry entry : chart.entries()) {
            printEntry(entry);
        }
        System.out.println();

        for (SpotifyChartEntry dropout : chart.dropouts()) {
            printDropout(dropout);
        }
    }

    private static void printEntry(SpotifyChartEntry entry) {
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
        System.out.println(s);
    }

    private static void printDropout(SpotifyChartEntry entry) {
        String s = String.format("-- (%02d) %d %s - %s [%s]",
                                 entry.position(),
                                 entry.weeksOnChart(),
                                 entry.artist(),
                                 entry.title(),
                                 getRun(entry.chartRun()));
        System.out.println(s);
    }

    private static String getRun(Set<ChartPosition> chartPositions) {
        return chartPositions.stream()
                .sorted(Comparator.comparingInt(ChartPosition::week))
                .map(pos -> Integer.toString(pos.position()))
                .collect(Collectors.joining(", "));
    }
}