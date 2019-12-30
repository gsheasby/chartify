package chart.postgres;

import chart.ChartRun;
import chart.Song;

import java.util.Iterator;
import java.util.List;

public class YearEndChartPrinter {
    public static void printYearEndChart(List<ChartRun> yec, int limit) {
        Iterator<ChartRun> yecIterator = yec.iterator();

        for (int pos = 1; yecIterator.hasNext() && pos <= limit; pos++) {
            ChartRun chartRun = yecIterator.next();
            printSingleSong(pos, chartRun);
        }
    }

    public static void printSingleSong(int pos, ChartRun chartRun) {
//        System.out.println(String.format("%02d\t%s", pos, chartRun.toString()));
        Song song = chartRun.getSong();
        System.out.println(String.format("%02d\t%03d\t%s\t%s\t-\t%s\t#%d\t%s\t%s",
                                         pos,
                                         chartRun.getScore(),
                                         getMarker(chartRun),
                                         song.title(),
                                         song.artist(),
                                         chartRun.getPeak(),
                                         chartRun.getEntryDate().toString("MMMM"),
                                         chartRun.getRun()));
    }

    private static String getMarker(ChartRun chartRun) {
        if (chartRun.isActive()) {
            return "*";
        }

        if (chartRun.getEntryDate().dayOfYear().get() <= 7) {
            return "J";
        }

        return "";
    }
}
