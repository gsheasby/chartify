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
            printWithStats(pos, chartRun);
//            printForPostage(pos, chartRun);
        }
    }

    public static void printForPostage(int pos, ChartRun chartRun) {
        String stringForPrinting = getBbCodedString(pos, chartRun);
        System.out.println(stringForPrinting);
    }

    public static String getBbCodedString(int pos, ChartRun chartRun) {
        Song song = chartRun.getSong();
        return String.format("[b]%02d[/b]\t%s\t-\t%s\t (#%d, %s)",
                pos,
                song.title(),
                song.artist(),
                chartRun.getPeak(),
                chartRun.getEntryDate().toString("MMMM"));
    }

    public static void printWithStats(int pos, ChartRun chartRun) {
//        System.out.println(String.format("%02d\t%s", pos, chartRun.toString()));
        Song song = chartRun.getSong();
        System.out.println(String.format("%02d\t%03d\t%d\t%d\t%s\t%s\t-\t%s\t%s\t%s",
                                         pos,
                                         chartRun.getScore(),
                                         chartRun.getWeeks(),
                                         chartRun.getPeak(),
                                         getMarker(chartRun),
                                         song.title(),
                                         song.artist(),
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
