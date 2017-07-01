package chart;

import java.io.IOException;

public class ChartCli {
    private static final String FOLDER = "src/main/resources";

    public static void main(String[] args) throws IOException {
        int week = Integer.parseInt(args[0]);
        ChartReader reader = new ChartReader(FOLDER, week);
        reader.findChart();
    }
}
