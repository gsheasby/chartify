package chart;

public class ChartPrinter {
    public static void print(Chart chart) {
        System.out.println(String.format("Week %d: %s",
                                         chart.week(),
                                         chart.date().toString("EEEE, MMMM dd, yyyy")));
        System.out.println();

        for (ChartEntry entry : chart.entries()) {
            printEntry(entry);
        }
        System.out.println();

        for (ChartEntry entry : chart.dropouts()) {
            printDropout(entry);
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
