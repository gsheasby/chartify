package chart;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ChartCompiler {
    private final ChartReader reader;

    public ChartCompiler(ChartReader reader) {
        this.reader = reader;
    }

    public Chart compileChart(int week) throws IOException {
        SimpleChart thisWeek = reader.findChart(week);

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
