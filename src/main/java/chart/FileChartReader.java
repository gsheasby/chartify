package chart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.joda.time.DateTime;

public class FileChartReader implements ChartReader {
    private final FileChartLoader fileChartLoader;

    public FileChartReader(String folder) {
        fileChartLoader = new FileChartLoader(folder);
    }

    @Override
    public Chart findDerivedChart(int week) throws IOException {
        Path chartPath = fileChartLoader.findFileForWeek(week);
        DateTime chartDate = ChartUtils.getDate(chartPath.getFileName().toString());
        Stream<String> lines = Files.lines(chartPath);
        List<ChartEntry> entries = lines.map(CsvLineParser::parseEntry).collect(Collectors.toList());
        return ImmutableChart.builder()
                                   .week(week)
                                   .date(chartDate)
                                   .entries(entries)
                                   .build();
    }

    @Override
    public SimpleChart findChart(int week) throws IOException {
        Path chartPath = fileChartLoader.findFileForWeek(week);
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
