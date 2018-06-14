package chart.postgres;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import chart.Chart;
import chart.ChartCompiler;
import chart.ChartEntry;
import chart.ChartReader;
import chart.SimpleChartEntry;
import chart.SpotifyChartReader;
import chart.spotify.ImmutableSpotifyChart;
import chart.spotify.ImmutableSpotifyChartEntry;
import chart.spotify.SimpleSpotifyChart;
import chart.spotify.SimpleSpotifyChartEntry;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;

public class PostgresChartCompiler implements ChartCompiler<SpotifyChart> {
    private final SpotifyChartReader reader;
    private final ChartReader lastWeekReader;
    private final int offset;

    public PostgresChartCompiler(SpotifyChartReader reader, ChartReader lastWeekReader) {
        this.reader = reader;
        this.lastWeekReader = lastWeekReader;
        this.offset = 7; // TODO custom offset
    }

    @Override
    public SpotifyChart compileChart() throws IOException {
        Chart lastWeek = lastWeekReader.findLatestChart();
        int lastWeekIndex = lastWeek.week();
        SimpleSpotifyChart thisWeek = reader.findChart(lastWeekIndex + 1);
        return compileChart(thisWeek, lastWeek);
    }

    @Override
    public SpotifyChart compileChart(int week) throws IOException {
        SimpleSpotifyChart thisWeek = reader.findChart(week);

        try {
            Chart lastWeek = lastWeekReader.findDerivedChart(week - 1);
            return compileChart(thisWeek, lastWeek);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return allNewEntries(thisWeek);
        }
    }

    private SpotifyChart compileChart(SimpleSpotifyChart thisWeek, Chart lastWeek) {
        List<SpotifyChartEntry> entries = new ArrayList<>();

        for (SimpleSpotifyChartEntry simpleEntry : thisWeek.entries()) {
            Optional<? extends ChartEntry> lastPos = findSongInChart(lastWeek, simpleEntry);
            int weeksOnChart = lastPos.map(chartEntry -> chartEntry.weeksOnChart() + 1).orElse(1);
            SpotifyChartEntry entry = ImmutableSpotifyChartEntry.builder()
                    .track(simpleEntry.track())
                    .position(simpleEntry.position())
                    .lastPosition(lastPos.map(ChartEntry::position))
                    .weeksOnChart(weeksOnChart)
                    .build();
            entries.add(entry);
        }

        Set<Integer> inFromLastWeek = lastWeek.entries().stream()
                                                 .map(ChartEntry::lastPosition)
                                                 .filter(Optional::isPresent)
                                                 .map(Optional::get)
                                                 .collect(Collectors.toSet());

        List<SpotifyChartEntry> dropouts = lastWeek.entries().stream()
                                                     .filter(ent -> !inFromLastWeek.contains(ent.position()))
                                                     .map(this::toSpotifyEntry)
                                                     .collect(Collectors.toList());

        return ImmutableSpotifyChart.builder()
                .week(thisWeek.week())
                .date(lastWeek.date().plusDays(offset))
                .entries(entries)
                .dropouts(dropouts)
                .build();
    }

    private SpotifyChartEntry toSpotifyEntry(ChartEntry chartEntry) {
        return ImmutableSpotifyChartEntry.builder()
                .from(chartEntry)
                .build();
    }

    private Optional<? extends ChartEntry> findSongInChart(Chart lastWeek, SimpleChartEntry entry) {
        return lastWeek.entries().stream()
                       .filter(sameSongAs(entry))
                       .findAny();
    }

    private Predicate<ChartEntry> sameSongAs(SimpleChartEntry entry) {
        return sce -> sce.artist().equalsIgnoreCase(entry.artist())
                && sce.title().equalsIgnoreCase(entry.title());
    }

    private SpotifyChart allNewEntries(SimpleSpotifyChart thisWeek) {
        List<SpotifyChartEntry> entries = thisWeek.entries().stream()
                .map(this::newEntry)
                .collect(Collectors.toList());

        return ImmutableSpotifyChart.builder()
                .week(thisWeek.week())
                .date(thisWeek.date())
                .entries(entries)
                .build();
    }

    private SpotifyChartEntry newEntry(SimpleSpotifyChartEntry simpleEntry) {
        return ImmutableSpotifyChartEntry.builder()
                                         .position(simpleEntry.position())
                                         .track(simpleEntry.track())
                                         .weeksOnChart(1)
                                         .build();
    }
}
