package chart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;

public class ChartReader {
    private final String folder;

    public ChartReader(String folder) {
        this.folder = folder;
    }

    public SimpleChart findChart(int week) throws IOException {
        Path path = Paths.get(folder);
        String weekStr = Integer.toString(week);
        List<Path> chartFiles = Files.walk(path)
                                  .filter(file -> file.getFileName().toString().startsWith(weekStr))
                                  .collect(Collectors.toList());
        if (chartFiles.isEmpty()) {
            throw new IllegalArgumentException("Chart for week " + week + " not found!");
        }

        Path chartPath = chartFiles.get(0);
        DateTime chartDate = ChartUtils.getDate(chartPath.getFileName().toString());
        Stream<String> lines = Files.lines(chartPath);
        List<SimpleChartEntry> entries = lines.map(CsvLineParser::parse).collect(Collectors.toList());
        return ImmutableSimpleChart.builder()
                                   .week(week)
                                   .date(chartDate)
                                   .entries(entries)
                                   .build();
    }
}
