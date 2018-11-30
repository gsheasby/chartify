package chart.tasks;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import chart.Chart;
import chart.ChartConfig;
import chart.ChartEntry;
import chart.Song;
import chart.postgres.PostgresChartLoader;
import chart.postgres.PostgresChartReader;
import chart.postgres.PostgresConnection;
import chart.postgres.PostgresConnectionManager;
import chart.spotify.SpotifyChart;

public class ChartOfChartsTask {
    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        int year = 2018;
        if (args.length < 1) {
            System.out.println("Using default year of " + year);
        } else {
            year = Integer.parseInt(args[0]);
        }

        ChartConfig config = TaskUtils.getConfig();
        PostgresConnectionManager manager = PostgresConnectionManager.create(config.postgresConfig());
        PostgresConnection connection = new PostgresConnection(manager);
        PostgresChartReader reader = new PostgresChartReader(connection);
        PostgresChartLoader loader = new PostgresChartLoader(connection, reader);
        List<SpotifyChart> charts = loader.loadCharts(year);

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
        System.out.println(String.format("%02d\t%s", pos, chartRun.toString()));
    }

    private static class ChartRun implements Comparable<ChartRun> {
        private static final int POSITIONS_TO_SCORE = 60;

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
            return positions.stream().filter(pos -> pos <= POSITIONS_TO_SCORE)
                     .map(pos -> POSITIONS_TO_SCORE + 1 - pos)
                     .mapToInt(Integer::intValue)
                     .sum();
        }

        private Integer getPeak() {
            //noinspection ConstantConditions
            return positions.stream().mapToInt(i -> i).min().getAsInt();
        }

        private Integer getWeeks() {
            return positions.size();
        }

        @Override
        public int compareTo(ChartRun o) {
            Integer score = getScore();
            Integer otherScore = o.getScore();
            if (!Objects.equals(score, otherScore)) {
                // Higher scores are better
                return -score.compareTo(otherScore);
            } else {
                Integer peak = getPeak();
                Integer otherPeak = o.getPeak();
                if (!Objects.equals(peak, otherPeak)) {
                    // Lower peaks are better
                    return peak.compareTo(otherPeak);
                } else {
                    // More weeks are better
                    return -getWeeks().compareTo(o.getWeeks());
                }
            }
        }

        @Override
        public String toString() {
            return String.format("%03d\t%02d\t%02d\t%s\t-\t%s\t%s\t%s",
                                 getScore(),
                                 positions.size(),
                                 getPeak(),
                                 song.title(),
                                 song.artist(),
                                 entryDate.toLocalDate(),
                                 positions.toString());

//                    getScore() + ": " + song.title() + " - " + song.artist()
//                    + " (" + positions.size() + " weeks; run: " + positions.toString() + ")";
        }

    }
}
