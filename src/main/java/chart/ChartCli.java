package chart;

import java.io.IOException;

public class ChartCli {
    private static final String FOLDER = "src/main/resources";

    public static void main(String[] args) throws IOException {
        int week = Integer.parseInt(args[0]);
        ChartReader reader = new ChartReader(FOLDER);
        ChartCompiler compiler = new ChartCompiler(reader);
        Chart chart = compiler.compileChart(week);

        for (ChartEntry entry : chart.entries()) {
            printEntry(entry);
        }
    }

    private static void printEntry(ChartEntry entry) {
        String s;
        if (entry.lastPosition().isPresent()) {
            s = String.format("%02d (%02d) %s - %s",
                              entry.position(),
                              entry.lastPosition().get(),
                              entry.title(),
                              entry.artist());
        } else {
            s = String.format("%02d (NE) %s - %s",
                              entry.position(),
                              entry.title(),
                              entry.artist());
        }
        System.out.println(s);
    }
}
