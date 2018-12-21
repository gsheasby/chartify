package chart.postgres;

import java.util.Iterator;
import java.util.List;

import chart.ChartRun;

public class YearEndChartPrinter {
    public static void printYearEndChart(List<ChartRun> yec) {
        Iterator<ChartRun> yecIterator = yec.iterator();

        for (int pos = 1; yecIterator.hasNext(); pos++) {
            ChartRun chartRun = yecIterator.next();
            printSingleSong(pos, chartRun);
        }
    }

    public static void printSingleSong(int pos, ChartRun chartRun) {
        System.out.println(String.format("%02d\t%s", pos, chartRun.toString()));
    }
}
