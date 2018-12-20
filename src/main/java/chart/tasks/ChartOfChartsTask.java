package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import chart.ChartConfig;
import chart.ChartRun;
import chart.Song;
import chart.postgres.MultiChartLoader;
import chart.postgres.YearEndChartPrinter;

public class ChartOfChartsTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        int year = 2018;
        if (args.length < 1) {
            System.out.println("Using default year of " + year);
        } else {
            year = Integer.parseInt(args[0]);
        }

        ChartConfig config = TaskUtils.getConfig();
        Map<Song, ChartRun> chartRuns = new MultiChartLoader(config).getAllChartRuns(year);

        List<ChartRun> yec = chartRuns.values().stream().sorted().collect(Collectors.toList());
        YearEndChartPrinter.printYearEndChart(yec);
    }
}
