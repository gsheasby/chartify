package chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChartCompiler {
    private final ChartReader reader;

    public ChartCompiler(ChartReader reader) {
        this.reader = reader;
    }

    public Chart compileChart(int week) throws IOException {
        SimpleChart thisWeek = reader.findChart(week);

        try {
            SimpleChart lastWeek = reader.findChart(week - 1);

            List<ChartEntry> entries = new ArrayList<>();

            for (SimpleChartEntry simpleEntry : thisWeek.entries()) {
                Optional<Integer> lastPos = findSongInChart(lastWeek, simpleEntry);
                int weeksOnChart = lastPos.isPresent() ? 2 : 1;
                ChartEntry entry = ImmutableChartEntry.builder()
                        .position(simpleEntry.position())
                        .title(simpleEntry.title())
                        .artist(simpleEntry.artist())
                        .weeksOnChart(weeksOnChart)
                        .lastPosition(lastPos)
                        .build();
                entries.add(entry);
            }

            return ImmutableChart.builder().entries(entries).build();

        } catch (IllegalArgumentException ex) {
            return allNewEntries(thisWeek);
        }
    }

    private Optional<Integer> findSongInChart(SimpleChart lastWeek, SimpleChartEntry entry) {
        return lastWeek.entries().stream()
                       .filter(sameSongAs(entry))
                       .map(SimpleChartEntry::position)
                       .findAny();
    }

    private Predicate<SimpleChartEntry> sameSongAs(SimpleChartEntry entry) {
        return sce -> sce.artist().equals(entry.artist()) && sce.title().equals(entry.title());
    }

    private Chart allNewEntries(SimpleChart thisWeek) {
        List<ChartEntry> entries = thisWeek.entries().stream()
                                           .map(this::newEntry)
                                           .collect(Collectors.toList());

        return ImmutableChart.builder().entries(entries).build();
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
