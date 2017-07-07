package chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChartCompiler {
    private final ChartReader reader;
    private final ChartReader derivedReader;

    public ChartCompiler(ChartReader reader, ChartReader derivedReader) {
        this.reader = reader;
        this.derivedReader = derivedReader;
    }

    public Chart compileChart(int week) throws IOException {
        SimpleChart thisWeek = reader.findChart(week);

        try {
            Chart lastWeek = derivedReader.findDerivedChart(week - 1);

            List<ChartEntry> entries = new ArrayList<>();

            for (SimpleChartEntry simpleEntry : thisWeek.entries()) {
                Optional<ChartEntry> lastPos = findSongInChart(lastWeek, simpleEntry);
                int weeksOnChart = lastPos.map(chartEntry -> chartEntry.weeksOnChart() + 1).orElse(1);
                ChartEntry entry = ImmutableChartEntry.builder()
                        .position(simpleEntry.position())
                        .title(simpleEntry.title())
                        .artist(simpleEntry.artist())
                        .weeksOnChart(weeksOnChart)
                        .lastPosition(lastPos.map(ChartEntry::position))
                        .build();
                entries.add(entry);
            }

            return ImmutableChart.builder()
                                 .week(thisWeek.week())
                                 .date(thisWeek.date())
                                 .entries(entries)
                                 .build();

        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return allNewEntries(thisWeek);
        }
    }

    private Optional<ChartEntry> findSongInChart(Chart lastWeek, SimpleChartEntry entry) {
        return lastWeek.entries().stream()
                       .filter(sameSongAs(entry))
                       .findAny();
    }

    private Predicate<ChartEntry> sameSongAs(SimpleChartEntry entry) {
        return sce -> sce.artist().equals(entry.artist()) && sce.title().equals(entry.title());
    }

    private Chart allNewEntries(SimpleChart thisWeek) {
        List<ChartEntry> entries = thisWeek.entries().stream()
                                           .map(this::newEntry)
                                           .collect(Collectors.toList());

        return ImmutableChart.builder().week(thisWeek.week())
                             .date(thisWeek.date())
                             .entries(entries).build();
    }

    private ChartEntry newEntry(SimpleChartEntry simpleEntry) {
        return ImmutableChartEntry.builder()
                .position(simpleEntry.position())
                .weeksOnChart(1)
                .title(simpleEntry.title())
                .artist(simpleEntry.artist())
                .build();
    }
}
