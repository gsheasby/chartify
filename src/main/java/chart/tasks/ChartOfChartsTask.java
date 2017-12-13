package chart.tasks;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import chart.Chart;
import chart.ChartConfig;
import chart.ChartEntry;
import chart.Song;
import chart.csv.CsvChartLoader;

public class ChartOfChartsTask {
    public static void main(String[] args) throws IOException {
        ChartConfig config = TaskUtils.getConfig();
        CsvChartLoader loader = new CsvChartLoader(config.csvDestination());
        List<Chart> charts = loader.loadAll();

        Map<Song, ChartRun> chartRuns = Maps.newHashMap();
        for (Chart chart : charts) {
            for (ChartEntry entry : chart.entries()) {
                Song song = Song.fromEntry(entry);
                if (chartRuns.containsKey(song)) {
                    chartRuns.get(song).add(entry.position());
                } else {
                    ChartRun chartRun = new ChartRun(song, chart.week(), chart.date());
                    chartRun.add(entry.position());
                    chartRuns.put(song, chartRun);
                }
            }
        }

        Iterator<ChartRun> chartOfCharts = chartRuns.values().stream().sorted().collect(Collectors.toList()).iterator();
        for (int pos = 1; chartOfCharts.hasNext(); pos++) {
            ChartRun chartRun = chartOfCharts.next();
            print(pos, chartRun);
        }
    }

    private static void print(int pos, ChartRun chartRun) {
        System.out.println(pos + ": " + chartRun.toString());
    }

    private static class ChartRun implements Comparable<ChartRun> {
        private final Song song;
        private final Integer entryWeek;
        private final DateTime entryDate;

        private List<Integer> positions;

        ChartRun(Song song, Integer entryWeek, DateTime entryDate) {
            this.song = song;
            this.entryWeek = entryWeek;
            this.entryDate = entryDate;
            this.positions = Lists.newArrayList();
        }

        void add(Integer position) {
            positions.add(position);
        }

        Integer getScore() {
            return positions.stream().filter(pos -> pos <= 30)
                     .map(pos -> 31 - pos)
                     .mapToInt(Integer::intValue)
                     .sum();
        }

        @Override
        public int compareTo(ChartRun o) {
            // Negative so we sort the scores descending order.
            return -getScore().compareTo(o.getScore());
        }

        @Override
        public String toString() {
            return getScore() + ": " + song.title() + " - " + song.artist()
                    + " (" + positions.size() + " weeks; run: " + positions.toString() + ")";
        }
    }
}
