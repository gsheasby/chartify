package chart.csv;

import chart.ChartReader;
import chart.ChartUtils;
import chart.FileReference;
import org.joda.time.DateTime;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileChartReader implements ChartReader<CsvChart, CsvSimpleChart> {
    private final FileChartLoader fileChartLoader;

    public FileChartReader(String folder) {
        fileChartLoader = new FileChartLoader(folder);
    }

    @Override
    public CsvChart findLatestChart() throws IOException {
        FileReference mostRecent = fileChartLoader.findMostRecent();
        return findDerivedChart(mostRecent);
    }

    @Override
    public CsvChart findDerivedChart(int week) throws IOException {
        Path chartPath = fileChartLoader.findFileForWeek(week);
        return findDerivedChart(FileReference.of(week, chartPath));
    }

    private CsvChart findDerivedChart(FileReference fileReference) throws IOException {
        DateTime chartDate = ChartUtils.getDate(fileReference.path().getFileName().toString());
        Stream<String> lines = Files.lines(fileReference.path());
        List<CsvChartEntry> entries = lines.map(CsvLineParser::parseEntry).collect(Collectors.toList());
        return ImmutableCsvChart.builder()
                                .week(fileReference.week())
                                .date(chartDate)
                                .entries(entries)
                                .build();
    }

    @Override
    public CsvSimpleChart findChart(int week) throws IOException {
        Path chartPath = fileChartLoader.findFileForWeek(week);
        DateTime chartDate = ChartUtils.getDate(chartPath.getFileName().toString());
        Stream<String> lines = Files.lines(chartPath);
        List<CsvSimpleChartEntry> entries = lines.map(CsvLineParser::parse).collect(Collectors.toList());
        return ImmutableCsvSimpleChart.builder()
                                      .week(week)
                                      .date(chartDate)
                                      .entries(entries)
                                      .build();
    }
}
