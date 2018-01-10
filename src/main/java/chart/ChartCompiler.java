package chart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ChartCompiler {
    private final SimpleChartReader reader;
    private final ChartReader derivedReader;

    public ChartCompiler(SimpleChartReader reader, ChartReader derivedReader) {
        this.reader = reader;
        this.derivedReader = derivedReader;
    }

    public Chart compileChart() throws IOException {
        Chart lastWeek = derivedReader.findLatestChart();
        int lastWeekIndex = lastWeek.week();
        SimpleChart thisWeek = reader.findChart(lastWeekIndex + 1);
        return compileChart(thisWeek, lastWeek);
    }

    public Chart compileChart(int week) throws IOException {
        SimpleChart thisWeek = reader.findChart(week);

        try {
            Chart lastWeek = derivedReader.findDerivedChart(week - 1);
            return compileChart(thisWeek, lastWeek);
        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
            return allNewEntries(thisWeek);
        }
    }

    private Chart compileChart(SimpleChart thisWeek, Chart lastWeek) {
        List<ChartEntry> entries = new ArrayList<>();

        for (SimpleChartEntry simpleEntry : thisWeek.entries()) {
            Optional<ChartEntry> lastPos = findSongInChart(lastWeek, simpleEntry);
            int weeksOnChart = lastPos.map(chartEntry -> chartEntry.weeksOnChart() + 1).orElse(1);
            SpotifyChartEntry entry = ImmutableSpotifyChartEntry.builder()
                                                  .position(simpleEntry.position())
                                                  .title(simpleEntry.title())
                                                  .artist(simpleEntry.artist())
                                                  .weeksOnChart(weeksOnChart)
                                                  .lastPosition(lastPos.map(ChartEntry::position))
                                                  .build();
            entries.add(entry);
        }

        Set<Integer> inFromLastWeek = entries.stream()
                                             .map(ent -> ent.lastPosition().orElse(-1))
                                             .filter(pos -> pos != -1) // bit of a hack
                                             .collect(Collectors.toSet());

        List<ChartEntry> dropouts = lastWeek.entries().stream()
                .filter(ent -> !inFromLastWeek.contains(ent.position()))
                .collect(Collectors.toList());

        return ImmutableChart.builder()
                             .week(thisWeek.week())
                             .date(lastWeek.date().plusDays(7))
                             .entries(entries)
                             .dropouts(dropouts)
                             .build();
    }

    private Optional<ChartEntry> findSongInChart(Chart lastWeek, SimpleChartEntry entry) {
        return lastWeek.entries().stream()
                       .filter(sameSongAs(entry))
                       .findAny();
    }

    private Predicate<ChartEntry> sameSongAs(SimpleChartEntry entry) {
        return sce -> sce.artist().equalsIgnoreCase(entry.artist())
                && sce.title().equalsIgnoreCase(entry.title());
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
