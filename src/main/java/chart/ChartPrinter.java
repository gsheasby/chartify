package chart;

import java.util.List;

public class ChartPrinter {
    public void print(Chart chart) {
        System.out.println(String.format("Week %d: %s",
                                         chart.week(),
                                         chart.date().toString("EEEE, MMMM dd, yyyy")));
        System.out.println();

        printEntries(chart.entries());
        System.out.println();

        printDropouts(chart.dropouts());
    }

    private void printDropouts(List<? extends ChartEntry> dropouts) {
        for (ChartEntry entry : dropouts) {
            printDropout(entry);
        }
    }

    private void printEntries(List<? extends ChartEntry> entries) {
        for (ChartEntry entry : entries) {
            printEntry(entry);
        }
    }

    private static void printEntry(ChartEntry entry) {
        String s;
        if (entry.lastPosition().isPresent()) {
            s = String.format("%02d (%02d) %d %s - %s",
                              entry.position(),
                              entry.lastPosition().get(),
                              entry.weeksOnChart(),
                              entry.artist(),
                              entry.title());
        } else {
            s = String.format("%02d (NE) %d %s - %s",
                              entry.position(),
                              entry.weeksOnChart(),
                              entry.artist(),
                              entry.title());
        }
        System.out.println(s);
    }

    private static void printDropout(ChartEntry entry) {
        String s = String.format("-- (%02d) %d %s - %s",
                          entry.position(),
                          entry.weeksOnChart(),
                          entry.artist(),
                          entry.title());
        System.out.println(s);
    }
}
