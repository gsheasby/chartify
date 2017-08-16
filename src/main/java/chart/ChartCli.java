package chart;

import java.io.IOException;

public class ChartCli {
    private static final String FOLDER = "src/main/resources";
    private static final String DERIVED_FOLDER = FOLDER + "/derived";

    public static void main(String[] args) throws IOException {
        int week = Integer.parseInt(args[0]);
        ChartReader reader = new ChartReader(FOLDER);
        ChartReader derivedReader = new ChartReader(DERIVED_FOLDER);
        ChartCompiler compiler = new ChartCompiler(reader, derivedReader);
        Chart chart = compiler.compileChart(week);

        new ChartSaver(DERIVED_FOLDER).saveChart(chart);

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
                              entry.title(),
                              entry.artist());
        } else {
            s = String.format("%02d (NE) %d %s - %s",
                              entry.position(),
                              entry.weeksOnChart(),
                              entry.title(),
                              entry.artist());
        }
        System.out.println(s);
    }

    private static void printDropout(ChartEntry entry) {
        String s = String.format("-- (%02d) %d %s - %s",
                          entry.position(),
                          entry.weeksOnChart(),
                          entry.title(),
                          entry.artist());
        System.out.println(s);
    }
}
