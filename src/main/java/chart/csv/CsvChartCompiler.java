package chart.csv;

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
import chart.SimpleChart;
import chart.SimpleChartEntry;
import chart.SimpleChartReader;

public class CsvChartCompiler implements ChartCompiler<CsvChart> {
    private final SimpleChartReader reader;
    private final FileChartReader derivedReader;
    private final int offset;

    public CsvChartCompiler(SimpleChartReader reader, FileChartReader derivedReader) {
        this(reader, derivedReader, 7);
    }

    public CsvChartCompiler(SimpleChartReader reader, FileChartReader derivedReader, int offset) {
        this.reader = reader;
        this.derivedReader = derivedReader;
        this.offset = offset;
    }

    @Override public CsvChart compileChart() throws IOException {
        CsvChart lastWeek = derivedReader.findLatestChart();
        int lastWeekIndex = lastWeek.week();
        SimpleChart thisWeek = reader.findChart(lastWeekIndex + 1);
        return compileChart(thisWeek, lastWeek);
    }

    @Override public CsvChart compileChart(int week) throws IOException {
        SimpleChart thisWeek = reader.findChart(week);

        try {
            CsvChart lastWeek = derivedReader.findDerivedChart(week - 1);
            return compileChart(thisWeek, lastWeek);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return allNewEntries(thisWeek);
        }
    }

    private CsvChart compileChart(SimpleChart thisWeek, CsvChart lastWeek) {
        List<CsvChartEntry> entries = new ArrayList<>();

        for (SimpleChartEntry simpleEntry : thisWeek.entries()) {
            Optional<? extends ChartEntry> lastPos = findSongInChart(lastWeek, simpleEntry);
            int weeksOnChart = lastPos.map(chartEntry -> chartEntry.weeksOnChart() + 1).orElse(1);
            CsvChartEntry entry = ImmutableCsvChartEntry.builder()
                                                        .position(simpleEntry.position())
                                                        .title(simpleEntry.title())
                                                        .artist(simpleEntry.artist())
                                                        .id(simpleEntry.id())
                                                        .href(simpleEntry.href())
                                                        .uri(simpleEntry.uri())
                                                        .weeksOnChart(weeksOnChart)
                                                        .lastPosition(lastPos.map(ChartEntry::position))
                                                        .build();
            entries.add(entry);
        }

        Set<Integer> inFromLastWeek = entries.stream()
                                             .map(ent -> ent.lastPosition().orElse(-1))
                                             .filter(pos -> pos != -1) // bit of a hack
                                             .collect(Collectors.toSet());

        List<CsvChartEntry> dropouts = lastWeek.entries().stream()
                .filter(ent -> !inFromLastWeek.contains(ent.position()))
                .collect(Collectors.toList());

        return ImmutableCsvChart.builder()
                                .week(thisWeek.week())
                                .date(lastWeek.date().plusDays(offset))
                                .entries(entries)
                                .dropouts(dropouts)
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

    private CsvChart allNewEntries(SimpleChart thisWeek) {
        List<CsvChartEntry> entries = thisWeek.entries().stream()
                                           .map(this::newEntry)
                                           .collect(Collectors.toList());

        return ImmutableCsvChart.builder().week(thisWeek.week())
                             .date(thisWeek.date())
                             .entries(entries).build();
    }

    private CsvChartEntry newEntry(SimpleChartEntry simpleEntry) {
        return ImmutableCsvChartEntry.builder()
                .position(simpleEntry.position())
                .weeksOnChart(1)
                .title(simpleEntry.title())
                .artist(simpleEntry.artist())
                .id(simpleEntry.id())
                .href(simpleEntry.href())
                .uri(simpleEntry.uri())
                .build();
    }
}
