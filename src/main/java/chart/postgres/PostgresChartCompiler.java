package chart.postgres;

import chart.ChartCompiler;
import chart.ChartEntry;
import chart.SimpleChartEntry;
import chart.spotify.ChartPosition;
import chart.spotify.ImmutableChartPosition;
import chart.spotify.ImmutableSpotifyChart;
import chart.spotify.ImmutableSpotifyChartEntry;
import chart.spotify.SimpleSpotifyChart;
import chart.spotify.SimpleSpotifyChartEntry;
import chart.spotify.SpotifyChart;
import chart.spotify.SpotifyChartEntry;
import chart.spotify.SpotifyChartReader;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PostgresChartCompiler implements ChartCompiler<SpotifyChart> {
    private final SpotifyChartReader reader;
    private final PostgresChartReader lastWeekReader;
    private final int offset;

    public PostgresChartCompiler(SpotifyChartReader reader, PostgresChartReader lastWeekReader) {
        this.reader = reader;
        this.lastWeekReader = lastWeekReader;
        this.offset = 7; // TODO custom offset
    }

    @Override
    public SpotifyChart compileChart() {
        SpotifyChart lastWeek = lastWeekReader.findLatestChart();
        int lastWeekIndex = lastWeek.week();
        SimpleSpotifyChart thisWeek = reader.findChart(lastWeekIndex + 1);
        return compileChart(thisWeek, lastWeek);
    }

    @Override
    public SpotifyChart compileChart(int week) {
        SimpleSpotifyChart thisWeek = reader.findChart(week);

        try {
            SpotifyChart lastWeek = lastWeekReader.findDerivedChart(week - 1);
            return compileChart(thisWeek, lastWeek);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return allNewEntries(thisWeek);
        }
    }

    private SpotifyChart compileChart(SimpleSpotifyChart thisWeek, SpotifyChart lastWeek) {
        List<SpotifyChartEntry> entries = new ArrayList<>();

        for (SimpleSpotifyChartEntry simpleEntry : thisWeek.entries()) {
            Optional<SpotifyChartEntry> lastPos = findSongInChart(lastWeek, simpleEntry);
            int weeksOnChart = lastPos.map(chartEntry -> chartEntry.weeksOnChart() + 1).orElse(1);
            Set<ChartPosition> chartPositions = lastPos.map(entry ->
                    Sets.newHashSet(entry.chartRun())).orElse(Sets.newHashSet());
            chartPositions.add(ImmutableChartPosition.builder()
                                                     .week(thisWeek.week())
                                                     .position(simpleEntry.position())
                                                     .build());

            SpotifyChartEntry entry = ImmutableSpotifyChartEntry.builder()
                    .track(simpleEntry.track())
                    .position(simpleEntry.position())
                    .lastPosition(lastPos.map(ChartEntry::position))
                    .weeksOnChart(weeksOnChart)
                    .chartRun(chartPositions)
                    .isYoutube(simpleEntry.isYoutube())
                    .build();
            entries.add(entry);
        }

        Set<Integer> inFromLastWeek = entries.stream()
                                                 .map(ChartEntry::lastPosition)
                                                 .filter(Optional::isPresent)
                                                 .map(Optional::get)
                                                 .collect(Collectors.toSet());

        List<SpotifyChartEntry> dropouts = lastWeek.entries().stream()
                                                     .filter(ent -> !inFromLastWeek.contains(ent.position()))
                                                     .collect(Collectors.toList());

        return ImmutableSpotifyChart.builder()
                .week(thisWeek.week())
                .date(lastWeek.date().plusDays(offset))
                .entries(entries)
                .dropouts(dropouts)
                .build();
    }

    private Optional<SpotifyChartEntry> findSongInChart(SpotifyChart lastWeek, SimpleChartEntry entry) {
        return lastWeek.entries().stream()
                       .filter(sameSongAs(entry))
                       .findAny();
    }

    private Predicate<ChartEntry> sameSongAs(SimpleChartEntry entry) {
        return sce -> sce.sameSongAs(entry);
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
